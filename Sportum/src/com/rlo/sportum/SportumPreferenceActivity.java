package com.rlo.sportum;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

import com.rlo.sportum.util.SportumUtil;
import com.rlo.sportum.util.numberpicker.NumberPicker;
import com.rlo.sportum.util.numberpicker.NumberPickerPreference;

/**
 * Pantalla que muestra todas las preferencias de la aplicación y que permite su modificación
 * y almacenamiento en el sistema.
 * 
 * @author rafa
 *
 */
public class SportumPreferenceActivity extends PreferenceActivity {

	private static final String TAG = SportumPreferenceActivity.class.getSimpleName();
		
	private static final int USUARIO_EDAD_DIALOGO_ID = 0;
	private static final int OBJETIVO_TIEMPO_DIALOGO_ID = 1;
	private static final int FRECUENCIA_TIEMPO_DIALOGO_ID = 2;
	private static final int ACERCA_DE_DIALOGO_ID = 3;
	
	private SportumUtil sportumUtil;
	private Resources res;
	
	private SharedPreferences preferencias;
	private Editor editPreferencias;
	
	private View usuarioEdadLayout;
	private View objetivoTiempoLayout;
	private View frecuenciaTiempoLayout;
	
	private EditTextPreference usuarioNombrePref;
	private ListPreference usuarioGeneroPref;
	private NumberPickerPreference usuarioPesoPref;
	private Preference usuarioEdadPref;
	private ListPreference tipoActividadPref;
	private ListPreference objetivoPref;
	private Preference objetivoTiempoPref;
	private NumberPickerPreference objetivoDistanciaPref;
	private NumberPickerPreference objetivoVueltasPref;
	private NumberPickerPreference objetivoEnergiaPref;
	private ListPreference tipoFrecuenciaPref;
	private Preference frecuenciaTiempoPref;
	private NumberPickerPreference frecuenciaDistanciaPref;
	private NumberPickerPreference frecuenciaVueltasPref;
	private NumberPickerPreference frecuenciaEnergiaPref;
	private CheckBoxPreference infoTiempoPref;
	private CheckBoxPreference infoDistanciaPref;
	private CheckBoxPreference infoVueltasPref;
	private CheckBoxPreference infoEnergiaPref;
	private Preference gpsPref;
	private Preference acercaDePref;
	
	private EditText usuarioNombreEditText;
	private DatePicker usuarioFechaNacDatePicker;
	private int usuarioFecNacAnyo;
	private int usuarioFecNacMes;
	private int usuarioFecNacDia;
	private int anyoSeleccionado;
	private int mesSeleccionado;
	private int diaSeleccionado;
	
	private NumberPicker objetivoHorasNumberPicker;
	private NumberPicker objetivoMinutosNumberPicker;
	private int objetivoHoras;
	private int objetivoMinutos;
	private NumberPicker frecuenciaHorasNumberPicker;
	private NumberPicker frecuenciaMinutosNumberPicker;
	private int frecuenciaHoras;
	private int frecuenciaMinutos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.d(TAG, "Creando SportumPreferenceActivity");
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		sportumUtil = new SportumUtil();
		res = getResources();
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);
		editPreferencias = preferencias.edit();
        
		inicializarPreferencias();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialogo;
		switch (id) {
		case USUARIO_EDAD_DIALOGO_ID:
			dialogo = crearDialogoUsuarioEdad();
			break;
		case OBJETIVO_TIEMPO_DIALOGO_ID:
			dialogo = crearDialogoObjetivoTiempo();
			break;
		case FRECUENCIA_TIEMPO_DIALOGO_ID:
			dialogo = crearDialogoFrecuenciaTiempo();
			break;
		case ACERCA_DE_DIALOGO_ID:
			dialogo = crearDialogoAcercaDe();
			break;
		default:
			dialogo = null;
		}
		return dialogo;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case USUARIO_EDAD_DIALOGO_ID:
			prepararDialogoUsuarioEdad(dialog);
			break;
		case OBJETIVO_TIEMPO_DIALOGO_ID:
			prepararDialogoObjetivoTiempo(dialog);
			break;
		case FRECUENCIA_TIEMPO_DIALOGO_ID:
			prepararDialogoFrecuenciaTiempo(dialog);
			break;
		}
	}
	
	private void inicializarPreferencias() {
		
		// Usuario -> Nombre
		
		usuarioNombrePref = (EditTextPreference) findPreference(res.getString(R.string.usuario_nombre_key));
		usuarioNombrePref.setSummary(preferencias.getString(
				res.getString(R.string.usuario_nombre_key),
				res.getString(R.string.usuario_nombre_default)));
		usuarioNombrePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary((String)newValue);
				return true;
			}
		});
		
		usuarioNombreEditText = usuarioNombrePref.getEditText();
		int maxLength = res.getInteger(R.integer.usuario_nombre_max);
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(maxLength);
		usuarioNombreEditText.setFilters(fArray);
		usuarioNombreEditText.setSingleLine(true);
		
		
		// Usuario -> Género
		
		usuarioGeneroPref = (ListPreference) findPreference(res.getString(R.string.usuario_genero_key));
		usuarioGeneroPref.setSummary(res.getString(
				res.getIdentifier(
						preferencias.getString(res.getString(R.string.usuario_genero_key), res.getString(R.string.usuario_genero_default)),
						"string",
						getPackageName())));
		usuarioGeneroPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(res.getString(res.getIdentifier((String)newValue, "string", getPackageName())));
				return true;
			}
		});
		
		
		// Usuario -> Peso
		
		usuarioPesoPref = (NumberPickerPreference) findPreference(res.getString(R.string.usuario_peso_key));
		usuarioPesoPref.setSummary(
				getString(R.string.usuario_peso_summary,
				preferencias.getInt(
						res.getString(R.string.usuario_peso_key),
						res.getInteger(R.integer.usuario_peso_default))));
		usuarioPesoPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(res.getString(R.string.usuario_peso_summary, (Integer)newValue));
				return true;
			}
		});
		
		
		// Usuario -> Edad
		
		usuarioEdadPref = findPreference(res.getString(R.string.usuario_edad_key));
		usuarioFecNacAnyo = preferencias.getInt(
				res.getString(R.string.usuario_fecha_nacimiento_anyo_key),
				res.getInteger(R.integer.usuario_fecha_nacimiento_anyo_default));
		usuarioFecNacMes = preferencias.getInt(
				res.getString(R.string.usuario_fecha_nacimiento_mes_key),
				res.getInteger(R.integer.usuario_fecha_nacimiento_mes_default));
		usuarioFecNacDia = preferencias.getInt(
				res.getString(R.string.usuario_fecha_nacimiento_dia_key),
				res.getInteger(R.integer.usuario_fecha_nacimiento_dia_default));
		usuarioEdadPref.setSummary(res.getString(
				R.string.usuario_edad_summary,
				sportumUtil.calcularEdad(usuarioFecNacAnyo, usuarioFecNacMes, usuarioFecNacDia)));
		usuarioEdadPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(USUARIO_EDAD_DIALOGO_ID);
				return true;
			}
		});
		
		
		// Tipo de Actividad
		
		tipoActividadPref = (ListPreference) findPreference(res.getString(R.string.tipo_actividad_key));
		tipoActividadPref.setSummary(res.getString(
				res.getIdentifier(
						preferencias.getString(res.getString(R.string.tipo_actividad_key), res.getString(R.string.tipo_actividad_default)),
						"string",
						getPackageName())));
		tipoActividadPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(res.getString(res.getIdentifier((String)newValue, "string", getPackageName())));
				return true;
			}
		});
		
		
		// Objetivo
		
		objetivoPref = (ListPreference) findPreference(res.getString(R.string.tipo_objetivo_key));
		objetivoPref.setSummary(res.getString(
				res.getIdentifier(
						preferencias.getString(res.getString(R.string.tipo_objetivo_key), res.getString(R.string.tipo_objetivo_default)),
						"string",
						getPackageName())));
		objetivoPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(res.getString(res.getIdentifier((String)newValue, "string", getPackageName())));
				habilitarObjetivo((String) newValue);
				return true;
			}
		});
		
		
		// Objetivo -> Tiempo
		
		objetivoTiempoPref = findPreference(res.getString(R.string.objetivo_tiempo_key));
		objetivoHoras = preferencias.getInt(
				res.getString(R.string.objetivo_horas_key),
				res.getInteger(R.integer.objetivo_horas_default));
		objetivoMinutos = preferencias.getInt(
				res.getString(R.string.objetivo_minutos_key),
				res.getInteger(R.integer.objetivo_minutos_default));
		objetivoTiempoPref.setSummary(res.getString(R.string.objetivo_tiempo_summary, objetivoHoras, objetivoMinutos));
		objetivoTiempoPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(OBJETIVO_TIEMPO_DIALOGO_ID);
				return true;
			}
		});
		
		
		// Objetivo -> Distancia
		
		objetivoDistanciaPref = (NumberPickerPreference) findPreference(res.getString(R.string.objetivo_distancia_key));
		objetivoDistanciaPref.setSummary(res.getString(
				R.string.objetivo_distancia_summary,
				preferencias.getInt(
						res.getString(R.string.objetivo_distancia_key),
						res.getInteger(R.integer.objetivo_distancia_default))));
		objetivoDistanciaPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.objetivo_distancia_summary, (Integer)newValue));
				return true;
			}
		});
		
		
		// Objetivo -> Vueltas
		
		objetivoVueltasPref = (NumberPickerPreference) findPreference(res.getString(R.string.objetivo_vueltas_key));
		objetivoVueltasPref.setSummary(res.getString(
				R.string.objetivo_vueltas_summary,
				preferencias.getInt(
						res.getString(R.string.objetivo_vueltas_key),
						res.getInteger(R.integer.objetivo_vueltas_default))));
		objetivoVueltasPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.objetivo_vueltas_summary, (Integer)newValue));
				return true;
			}
		});
		
		
		// Objetivo -> Energía
		
		objetivoEnergiaPref = (NumberPickerPreference) findPreference(res.getString(R.string.objetivo_energia_key));
		objetivoEnergiaPref.setSummary(getString(
				R.string.objetivo_energia_summary,
				preferencias.getInt(
						res.getString(R.string.objetivo_energia_key),
						res.getInteger(R.integer.objetivo_energia_default))));
		objetivoEnergiaPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.objetivo_energia_summary, (Integer)newValue));
				return true;
			}
		});
		
		
		// Frecuencia
		
		tipoFrecuenciaPref = (ListPreference) findPreference(res.getString(R.string.tipo_frecuencia_key));
		tipoFrecuenciaPref.setSummary(res.getString(
				res.getIdentifier(
						preferencias.getString(res.getString(R.string.tipo_frecuencia_key), res.getString(R.string.tipo_frecuencia_default)),
						"string",
						getPackageName())));
		tipoFrecuenciaPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(res.getString(res.getIdentifier((String)newValue, "string", getPackageName())));
				habilitarFrecuencia((String) newValue);
				return true;
			}
		});
		
		
		// Frecuencia -> Tiempo
		
		frecuenciaTiempoPref = findPreference(res.getString(R.string.frecuencia_tiempo_key));
		frecuenciaHoras = preferencias.getInt(
				res.getString(R.string.frecuencia_horas_key),
				res.getInteger(R.integer.frecuencia_horas_default));
		frecuenciaMinutos = preferencias.getInt(
				res.getString(R.string.frecuencia_minutos_key),
				res.getInteger(R.integer.frecuencia_minutos_default));
		frecuenciaTiempoPref.setSummary(res.getString(R.string.frecuencia_tiempo_summary, frecuenciaHoras, frecuenciaMinutos));
		frecuenciaTiempoPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(FRECUENCIA_TIEMPO_DIALOGO_ID);
				return true;
			}
		});
		
		
		// Frecuencia -> Distancia
		
		frecuenciaDistanciaPref = (NumberPickerPreference) findPreference(res.getString(R.string.frecuencia_distancia_key));
		frecuenciaDistanciaPref.setSummary(res.getString(
				R.string.frecuencia_distancia_summary,
				preferencias.getInt(
						res.getString(R.string.frecuencia_distancia_key),
						res.getInteger(R.integer.frecuencia_distancia_default))));
		frecuenciaDistanciaPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.frecuencia_distancia_summary, (Integer)newValue));
				return true;
			}
		});
		
		
		// Frecuencia -> Vueltas
		
		frecuenciaVueltasPref = (NumberPickerPreference) findPreference(res.getString(R.string.frecuencia_vueltas_key));
		frecuenciaVueltasPref.setSummary(res.getString(
				R.string.frecuencia_vueltas_summary,
				preferencias.getInt(
						res.getString(R.string.frecuencia_vueltas_key),
						res.getInteger(R.integer.frecuencia_vueltas_default))));
		frecuenciaVueltasPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.frecuencia_vueltas_summary, (Integer)newValue));
				return true;
			}
		});
		
		
		// Frecuencia -> Energía
		
		frecuenciaEnergiaPref = (NumberPickerPreference) findPreference(res.getString(R.string.frecuencia_energia_key));
		frecuenciaEnergiaPref.setSummary(res.getString(
				R.string.frecuencia_energia_summary,
				preferencias.getInt(
						res.getString(R.string.frecuencia_energia_key),
						res.getInteger(R.integer.frecuencia_energia_default))));
		frecuenciaEnergiaPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.frecuencia_energia_summary, (Integer)newValue));
				return true;
			}
		});
		
		
		// Información Audio
		
		infoTiempoPref = (CheckBoxPreference) findPreference(res.getString(R.string.info_tiempo_key));
		infoDistanciaPref = (CheckBoxPreference) findPreference(res.getString(R.string.info_distancia_key));
		infoVueltasPref = (CheckBoxPreference) findPreference(res.getString(R.string.info_vueltas_key));
		infoEnergiaPref = (CheckBoxPreference) findPreference(res.getString(R.string.info_energia_key));
		
		
		habilitarObjetivo(preferencias.getString(
				res.getString(R.string.tipo_objetivo_key),
				res.getString(R.string.tipo_objetivo_default)));
		
		habilitarFrecuencia(preferencias.getString(
				res.getString(R.string.tipo_frecuencia_key),
				res.getString(R.string.tipo_frecuencia_default)));
		
		
		// GPS
		
		gpsPref = (Preference) findPreference(res.getString(R.string.gps_key));
		gpsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(i);
				return true;
			}
		});
		
		acercaDePref = (Preference) findPreference(res.getString(R.string.acerca_de_key));
		acercaDePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(ACERCA_DE_DIALOGO_ID);
				return true;
			}
		});
	}
	
	private Dialog crearDialogoUsuarioEdad() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		usuarioEdadLayout = inflater.inflate(R.layout.usuario_edad, null);
		usuarioFechaNacDatePicker = (DatePicker) usuarioEdadLayout.findViewById(R.id.fecha_nac);
		usuarioFechaNacDatePicker.init(usuarioFecNacAnyo, usuarioFecNacMes, usuarioFecNacDia, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if (sportumUtil.esFechaNacimientoValida(year, monthOfYear, dayOfMonth)) {
					anyoSeleccionado = year;
					mesSeleccionado = monthOfYear;
					diaSeleccionado = dayOfMonth;
				}
				else {
					usuarioFechaNacDatePicker.updateDate(anyoSeleccionado, mesSeleccionado, diaSeleccionado);
				}
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(usuarioEdadLayout);
		builder.setTitle(R.string.usuario_fecha_nacimiento_title);
		builder.setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				guardarFechaNacimiento();
			}
		});
		builder.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		return builder.create();
	}
	
	private Dialog crearDialogoObjetivoTiempo() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		objetivoTiempoLayout = inflater.inflate(R.layout.objetivo_tiempo, null);
		objetivoHorasNumberPicker = (NumberPicker) objetivoTiempoLayout.findViewById(R.id.objetivo_horas);
		objetivoMinutosNumberPicker = (NumberPicker) objetivoTiempoLayout.findViewById(R.id.objetivo_minutos);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(objetivoTiempoLayout);
		builder.setTitle(R.string.objetivo_tiempo_title_dialogo);
		builder.setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				guardarObjetivoTiempo();
			}
		});
		builder.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		return builder.create();
	}
	
	private Dialog crearDialogoFrecuenciaTiempo() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		frecuenciaTiempoLayout = inflater.inflate(R.layout.frecuencia_tiempo, null);
		frecuenciaHorasNumberPicker = (NumberPicker) frecuenciaTiempoLayout.findViewById(R.id.frecuencia_horas);
		frecuenciaMinutosNumberPicker = (NumberPicker) frecuenciaTiempoLayout.findViewById(R.id.frecuencia_minutos);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(frecuenciaTiempoLayout);
		builder.setTitle(R.string.frecuencia_tiempo_title_dialogo);
		builder.setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				guardarFrecuenciaTiempo();
			}
		});
		builder.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		return builder.create();
	}
	
	private Dialog crearDialogoAcercaDe() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(res.getString(R.string.acerca_de_title));
		builder.setMessage(res.getString(R.string.licencia));

		return builder.create();
	}
	
	private void prepararDialogoUsuarioEdad(Dialog dialog) {
		anyoSeleccionado = usuarioFecNacAnyo;
		mesSeleccionado = usuarioFecNacMes;
		diaSeleccionado = usuarioFecNacDia;
		usuarioFechaNacDatePicker.updateDate(usuarioFecNacAnyo, usuarioFecNacMes, usuarioFecNacDia);
	}
	
	private void prepararDialogoObjetivoTiempo(Dialog dialog) {
		objetivoHorasNumberPicker.setCurrent(objetivoHoras);
		objetivoMinutosNumberPicker.setCurrent(objetivoMinutos);
	}
	
	private void prepararDialogoFrecuenciaTiempo(Dialog dialog) {
		frecuenciaHorasNumberPicker.setCurrent(frecuenciaHoras);
		frecuenciaMinutosNumberPicker.setCurrent(frecuenciaMinutos);
	}
	
	private void guardarFechaNacimiento() {
		usuarioFecNacAnyo = usuarioFechaNacDatePicker.getYear();
		usuarioFecNacMes = usuarioFechaNacDatePicker.getMonth();
		usuarioFecNacDia = usuarioFechaNacDatePicker.getDayOfMonth();
		
		editPreferencias.putInt(res.getString(R.string.usuario_fecha_nacimiento_anyo_key), usuarioFecNacAnyo);
		editPreferencias.putInt(res.getString(R.string.usuario_fecha_nacimiento_mes_key), usuarioFecNacMes);
		editPreferencias.putInt(res.getString(R.string.usuario_fecha_nacimiento_dia_key), usuarioFecNacDia);
		editPreferencias.commit();
		
		usuarioEdadPref.setSummary(res.getString(
				R.string.usuario_edad_summary,
				sportumUtil.calcularEdad(usuarioFecNacAnyo, usuarioFecNacMes, usuarioFecNacDia)));
		
	}
	
	private void guardarObjetivoTiempo() {
		objetivoHoras = objetivoHorasNumberPicker.getCurrent();
		objetivoMinutos = objetivoMinutosNumberPicker.getCurrent();
		
		editPreferencias.putInt(res.getString(R.string.objetivo_horas_key), objetivoHoras);
		editPreferencias.putInt(res.getString(R.string.objetivo_minutos_key), objetivoMinutos);
		editPreferencias.commit();
		
		objetivoTiempoPref.setSummary(res.getString(R.string.objetivo_tiempo_summary, objetivoHoras, objetivoMinutos));
	}
	
	private void guardarFrecuenciaTiempo() {
		frecuenciaHoras = frecuenciaHorasNumberPicker.getCurrent();
		frecuenciaMinutos = frecuenciaMinutosNumberPicker.getCurrent();
		
		editPreferencias.putInt(res.getString(R.string.frecuencia_horas_key), frecuenciaHoras);
		editPreferencias.putInt(res.getString(R.string.frecuencia_minutos_key), frecuenciaMinutos);
		editPreferencias.commit();
		
		frecuenciaTiempoPref.setSummary(res.getString(R.string.frecuencia_tiempo_summary, frecuenciaHoras, frecuenciaMinutos));
	}
	
	private void habilitarObjetivo(String objetivo) {
		objetivoTiempoPref.setEnabled(objetivo.equals(res.getString(R.string.objetivo_tiempo_key)));
		objetivoDistanciaPref.setEnabled(objetivo.equals(res.getString(R.string.objetivo_distancia_key)));
		objetivoVueltasPref.setEnabled(objetivo.equals(res.getString(R.string.objetivo_vueltas_key)));
		objetivoEnergiaPref.setEnabled(objetivo.equals(res.getString(R.string.objetivo_energia_key)));
	}
	
	private void habilitarFrecuencia(String tipoFrecuencia) {
		frecuenciaTiempoPref.setEnabled(tipoFrecuencia.equals(res.getString(R.string.frecuencia_tiempo_key)));
		frecuenciaDistanciaPref.setEnabled(tipoFrecuencia.equals(res.getString(R.string.frecuencia_distancia_key)));
		frecuenciaVueltasPref.setEnabled(tipoFrecuencia.equals(res.getString(R.string.frecuencia_vueltas_key)));
		frecuenciaEnergiaPref.setEnabled(tipoFrecuencia.equals(res.getString(R.string.frecuencia_energia_key)));

		infoTiempoPref.setEnabled(!tipoFrecuencia.equals(res.getString(R.string.info_audio_desactivado_key)));
		infoDistanciaPref.setEnabled(!tipoFrecuencia.equals(res.getString(R.string.info_audio_desactivado_key)));
		infoVueltasPref.setEnabled(!tipoFrecuencia.equals(res.getString(R.string.info_audio_desactivado_key)));
		infoEnergiaPref.setEnabled(!tipoFrecuencia.equals(res.getString(R.string.info_audio_desactivado_key)));
	}
	
}
