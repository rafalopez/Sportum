package com.rlo.sportum.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.TextView;

import com.rlo.sportum.R;
import com.rlo.sportum.modelo.Actividad;
import com.rlo.sportum.modelo.InfoAudio;
import com.rlo.sportum.modelo.Objetivo;
import com.rlo.sportum.modelo.Usuario;

public class SportumPrefUtil {

	private Activity mActivity;
	private Context mCtx;
	private Resources mRes;
	private SharedPreferences mPref;
	private SportumUtil mSportumUtil;
	private Usuario mUsuario;
	private Actividad mActividad;
	private Objetivo mObjetivo;
	private InfoAudio mInfoAudio;
	private TextView mUsuarioNombreTxt;
	private ImageView mActividadImg;
	private ImageView mObjetivoImg;
	private ImageView mInfoAudioImg;

	public SportumPrefUtil(Activity activity, Context ctx, Resources res, SharedPreferences pref,
			SportumUtil sportumUtil, Actividad actividad, Usuario usuario, Objetivo objetivo, InfoAudio infoAudio,
			TextView usuarioNombreTxt, ImageView actividadImg, ImageView objetivoImg, ImageView infoAudioImg) {
		mActivity = activity;
		mCtx = ctx;
		mRes = res;
		mPref = pref;
		mSportumUtil = sportumUtil;
		mActividad = actividad;
		mUsuario = usuario;
		mObjetivo = objetivo;
		mInfoAudio = infoAudio;
		mUsuarioNombreTxt = usuarioNombreTxt;
		mActividadImg = actividadImg;
		mObjetivoImg = objetivoImg;
		mInfoAudioImg = infoAudioImg;
	}

	public void inicializarPreferencias() {
		establecerUsuarioNombre();
		establecerUsuarioGenero();
		establecerUsuarioPeso();
		establecerUsuarioEdad();
		establecerTipoActividad();
		establecerTipoObjetivo();
		establecerObjetivoTiempo();
		establecerObjetivoDistancia();
		establecerObjetivoVueltas();
		establecerObjetivoEnergia();
		establecerTipoFrecuencia();
		establecerFrecuenciaTiempo();
		establecerFrecuenciaDistancia();
		establcerFrecuenciaVueltas();
		establcerFrecuenciaEnergia();
		establecerInfoTiempo();
		establecerInfoDistancia();
		establecerInfoVueltas();
		establecerInfoEnergia();
		establecerPantallaOrientacion();
		establecerPantallaMantenerEncendida();
		establecerPantallaCompleta();
	}

	/**
	 * Vuelve a recuperar el valor de una preferencia cuya clave es key.
	 * 
	 * @param key
	 *            La clave que se ha cambiado y para la cual se quiere recuperar
	 *            el valor actualizado.
	 */
	public void actualizarDatos(String key) {
		if (key.equals(mRes.getString(R.string.usuario_nombre_key))) {
			establecerUsuarioNombre();
		} else if (key.equals(mRes.getString(R.string.usuario_genero_key))) {
			establecerUsuarioGenero();
		} else if (key.equals(mRes.getString(R.string.usuario_peso_key))) {
			establecerUsuarioPeso();
		} else if (key.equals(mRes
				.getString(R.string.usuario_fecha_nacimiento_anyo_key))
				|| key.equals(mRes.getString(R.string.usuario_fecha_nacimiento_mes_key))
				|| key.equals(mRes.getString(R.string.usuario_fecha_nacimiento_dia_key))) {
			establecerUsuarioEdad();
		} else if (key.equals(mRes.getString(R.string.tipo_actividad_key))) {
			establecerTipoActividad();
		} else if (key.equals(mRes.getString(R.string.tipo_objetivo_key))) {
			establecerTipoObjetivo();
		} else if (key.equals(mRes.getString(R.string.objetivo_horas_key))
				|| key.equals(mRes.getString(R.string.objetivo_minutos_key))) {
			establecerObjetivoTiempo();
		} else if (key.equals(mRes.getString(R.string.objetivo_distancia_key))) {
			establecerObjetivoDistancia();
		} else if (key.equals(mRes.getString(R.string.objetivo_vueltas_key))) {
			establecerObjetivoVueltas();
		} else if (key.equals(mRes.getString(R.string.objetivo_energia_key))) {
			establecerObjetivoEnergia();
		} else if (key.equals(mRes.getString(R.string.tipo_frecuencia_key))) {
			establecerTipoFrecuencia();
		} else if (key.equals(mRes.getString(R.string.frecuencia_horas_key))
				|| key.equals(mRes.getString(R.string.frecuencia_minutos_key))) {
			establecerFrecuenciaTiempo();
		} else if (key.equals(mRes.getString(R.string.frecuencia_distancia_key))) {
			establecerFrecuenciaDistancia();
		} else if (key.equals(mRes.getString(R.string.frecuencia_vueltas_key))) {
			establcerFrecuenciaVueltas();
		} else if (key.equals(mRes.getString(R.string.frecuencia_energia_key))) {
			establcerFrecuenciaEnergia();
		} else if (key.equals(mRes.getString(R.string.info_tiempo_key))) {
			establecerInfoTiempo();
		} else if (key.equals(mRes.getString(R.string.info_distancia_key))) {
			establecerInfoDistancia();
		} else if (key.equals(mRes.getString(R.string.info_vueltas_key))) {
			establecerInfoVueltas();
		} else if (key.equals(mRes.getString(R.string.info_energia_key))) {
			establecerInfoEnergia();
		} else if (key.equals(mRes.getString(R.string.pantalla_orientacion_key))) {
			establecerPantallaOrientacion();
		} else if (key.equals(mRes.getString(R.string.pantalla_mantener_encendida_key))) {
			establecerPantallaMantenerEncendida();
		} else if (key.equals(mRes.getString(R.string.pantalla_completa_key))) {
			establecerPantallaCompleta();
		}
	}

	private void establecerUsuarioNombre() {
		mUsuario.setNombre(mPref.getString(
				mRes.getString(R.string.usuario_nombre_key),
				mRes.getString(R.string.usuario_nombre_default)));

		mUsuarioNombreTxt.setText(mUsuario.getNombre());
	}

	private void establecerUsuarioGenero() {
		mUsuario.setGenero(mPref.getString(
				mRes.getString(R.string.usuario_genero_key),
				mRes.getString(R.string.usuario_genero_default)));
	}

	private void establecerUsuarioPeso() {
		mUsuario.setPeso(mPref.getInt(
				mRes.getString(R.string.usuario_peso_key),
				mRes.getInteger(R.integer.usuario_peso_default)));
	}

	private void establecerUsuarioEdad() {
		mUsuario.setEdad(mSportumUtil.calcularEdad(
				mPref.getInt(
						mRes.getString(R.string.usuario_fecha_nacimiento_anyo_key),
						mRes.getInteger(R.integer.usuario_fecha_nacimiento_anyo_default)),
				mPref.getInt(
						mRes.getString(R.string.usuario_fecha_nacimiento_mes_key),
						mRes.getInteger(R.integer.usuario_fecha_nacimiento_mes_default)),
				mPref.getInt(
						mRes.getString(R.string.usuario_fecha_nacimiento_dia_key),
						mRes.getInteger(R.integer.usuario_fecha_nacimiento_dia_default))));
	}

	private void establecerTipoActividad() {
		mActividad.setTipoActividad(mPref.getString(
				mRes.getString(R.string.tipo_actividad_key),
				mRes.getString(R.string.tipo_actividad_default)));

		mActividadImg.setImageDrawable(mRes.getDrawable(mRes.getIdentifier("ic_"
				+ mActividad.getTipoActividad(), "drawable",
				mCtx.getPackageName())));
	}

	private void establecerTipoObjetivo() {
		mObjetivo.setTipoObjetivo(mPref.getString(
				mRes.getString(R.string.tipo_objetivo_key),
				mRes.getString(R.string.tipo_objetivo_default)));

		mObjetivoImg.setImageDrawable(mRes.getDrawable(mRes.getIdentifier("ic_"
				+ mObjetivo.getTipoObjetivo(), "drawable",
				mCtx.getPackageName())));
	}

	private void establecerObjetivoTiempo() {
		int objetivoHoras = mPref.getInt(
				mRes.getString(R.string.objetivo_horas_key),
				mRes.getInteger(R.integer.objetivo_horas_default));
		int objetivoMinutos = mPref.getInt(
				mRes.getString(R.string.objetivo_minutos_key),
				mRes.getInteger(R.integer.objetivo_minutos_default));

		mObjetivo.setTiempo((60 * 60 * objetivoHoras) + (60 * objetivoMinutos));

	}

	private void establecerObjetivoDistancia() {
		mObjetivo.setDistancia(1000.0f * mPref.getInt(
				mRes.getString(R.string.objetivo_distancia_key),
				mRes.getInteger(R.integer.objetivo_distancia_default)));
	}

	private void establecerObjetivoVueltas() {
		mObjetivo.setVueltas(mPref.getInt(
				mRes.getString(R.string.objetivo_vueltas_key),
				mRes.getInteger(R.integer.objetivo_vueltas_default)));
	}

	private void establecerObjetivoEnergia() {
		mObjetivo.setEnergia(mPref.getInt(
				mRes.getString(R.string.objetivo_energia_key),
				mRes.getInteger(R.integer.objetivo_energia_default)));
	}

	private void establecerTipoFrecuencia() {
		mInfoAudio.setTipoFrecuencia(mPref.getString(
				mRes.getString(R.string.tipo_frecuencia_key),
				mRes.getString(R.string.tipo_frecuencia_default)));

		if (mInfoAudio.getTipoFrecuencia().equals(
				mRes.getString(R.string.info_audio_desactivado_key))) {
			mInfoAudioImg.setImageDrawable(mRes.getDrawable(mRes.getIdentifier(
					"ic_" + mInfoAudio.getTipoFrecuencia(), "drawable",
					mCtx.getPackageName())));
		} else {
			mInfoAudioImg.setImageDrawable(mRes.getDrawable(mRes.getIdentifier(
					"ic_" + mRes.getString(R.string.info_audio_activado_key),
					"drawable", mCtx.getPackageName())));
		}
	}

	private void establecerFrecuenciaTiempo() {
		int frecuenciaHoras = mPref.getInt(
				mRes.getString(R.string.frecuencia_horas_key),
				mRes.getInteger(R.integer.frecuencia_horas_default));
		int frecuenciaMinutos = mPref.getInt(
				mRes.getString(R.string.frecuencia_minutos_key),
				mRes.getInteger(R.integer.frecuencia_minutos_default));

		mInfoAudio.setFrecuenciaTiempo((60 * 60 * frecuenciaHoras)
				+ (60 * frecuenciaMinutos));
	}

	private void establecerFrecuenciaDistancia() {
		mInfoAudio.setFrecuenciaDistancia(1000.0f * mPref.getInt(
				mRes.getString(R.string.frecuencia_distancia_key),
				mRes.getInteger(R.integer.frecuencia_distancia_default)));
	}

	private void establcerFrecuenciaVueltas() {
		mInfoAudio.setFrecuenciaVueltas(mPref.getInt(
				mRes.getString(R.string.frecuencia_vueltas_key),
				mRes.getInteger(R.integer.frecuencia_vueltas_default)));
	}

	private void establcerFrecuenciaEnergia() {
		mInfoAudio.setFrecuenciaEnergia(mPref.getInt(
				mRes.getString(R.string.frecuencia_energia_key),
				mRes.getInteger(R.integer.frecuencia_energia_default)));
	}

	private void establecerInfoTiempo() {
		mInfoAudio.setInfoTiempo(mPref.getBoolean(
				mRes.getString(R.string.info_tiempo_key),
				mRes.getBoolean(R.bool.info_tiempo_default)));
	}

	private void establecerInfoDistancia() {
		mInfoAudio.setInfoDistancia(mPref.getBoolean(
				mRes.getString(R.string.info_distancia_key),
				mRes.getBoolean(R.bool.info_distancia_default)));
	}

	private void establecerInfoVueltas() {
		mInfoAudio.setInfoVueltas(mPref.getBoolean(
				mRes.getString(R.string.info_vueltas_key),
				mRes.getBoolean(R.bool.info_vueltas_default)));
	}

	private void establecerInfoEnergia() {
		mInfoAudio.setInfoEnergia(mPref.getBoolean(
				mRes.getString(R.string.info_energia_key),
				mRes.getBoolean(R.bool.info_energia_default)));
	}

	private void establecerPantallaOrientacion() {
		String orientacion = mPref.getString(
				mRes.getString(R.string.pantalla_orientacion_key),
				mRes.getString(R.string.pantalla_orientacion_default));

		if (orientacion.equals(mRes.getString(R.string.orientacion_landscape))) {
			mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	private void establecerPantallaMantenerEncendida() {
		mSportumUtil.mantenerPantallaEncendida(mActivity, mPref.getBoolean(
				mRes.getString(R.string.pantalla_mantener_encendida_key),
				mRes.getBoolean(R.bool.pantalla_mantener_encendida_default)));
	}

	private void establecerPantallaCompleta() {
		mSportumUtil.pantallaCompleta(mActivity, mPref.getBoolean(
				mRes.getString(R.string.pantalla_completa_key),
				mRes.getBoolean(R.bool.pantalla_completa_default)));
	}
}
