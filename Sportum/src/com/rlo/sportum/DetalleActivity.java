package com.rlo.sportum;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rlo.sportum.db.SportumDB;
import com.rlo.sportum.modelo.Actividad;
import com.rlo.sportum.util.SportumFileUtil;
import com.rlo.sportum.util.SportumUtil;

/**
 * Muestra toda la información de una actividad almacenada en BD.
 * Crea un menú con tres opciones que permiten: borrar la actividad,
 * mostrar el recorrido realizado sobre un mapa o visualizar una gráfica
 * con la elevació y velocidad a lo largo del recorrido.
 * 
 * @author rafa
 *
 */
public class DetalleActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = DetalleActivity.class.getSimpleName();
	
	private static final int CONFIRMAR_BORRADO_DIALOG_ID = 0;
	
	private Resources mRes;
	private SportumDB mSportumDB;
	private SportumUtil mSportumUtil;
	private SportumFileUtil mSportumFileUtil;
	private long mId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detalle);
		
		inicializarDatos();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.detalle_menu, menu);
	    return true;
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case CONFIRMAR_BORRADO_DIALOG_ID:
			dialog = crearDialogoConfirmarBorrado(id);
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
	    case R.id.borrar_actividad:
	    	showDialog(CONFIRMAR_BORRADO_DIALOG_ID);
	        return true;
	    case R.id.mostrar_recorrido:
	    	i = new Intent(this, MapaActivity.class);
	    	i.putExtra("id", mId);
	    	startActivity(i);
	        return true;
	    case R.id.mostrar_grafica:
	    	GraficaActivity graficaActivity = new GraficaActivity(this, mRes, mId);
	    	i = graficaActivity.execute(this);
	    	if (i != null) {
	    		startActivity(i);
	    	}
	    	else {
	    		Toast.makeText(getApplicationContext(), mRes.getString(R.string.error_datos), Toast.LENGTH_LONG).show();
	    	}
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Obtiene la actividad de la BD y muestra su información por pantalla.
	 */
	private void inicializarDatos() {
		mRes = getResources();
		mSportumUtil = new SportumUtil();
		mSportumFileUtil = new SportumFileUtil(this);
		SimpleDateFormat df = new SimpleDateFormat(mRes.getString(R.string.formato_fecha_detalle));
		
		Intent i = getIntent();
		mId = i.getLongExtra("id", 0);
		
		mSportumDB = new SportumDB(this);
		mSportumDB.abrir();
		
		Actividad actividad = mSportumDB.obtenerActividad(mId);
		
		ImageView actividadImg = (ImageView) findViewById(R.id.detalle_actividad_img);
		TextView actividadTxt = (TextView) findViewById(R.id.detalle_actividad_txt);
		TextView fechaTxt = (TextView) findViewById(R.id.detalle_fecha_txt);
		TextView tiempoTxt = (TextView) findViewById(R.id.detalle_tiempo_txt);
		TextView distanciaTxt = (TextView) findViewById(R.id.detalle_distancia_txt);
		TextView velocidadTxt = (TextView) findViewById(R.id.detalle_velocidad_txt);
		TextView vueltasTxt = (TextView) findViewById(R.id.detalle_vueltas_txt);
		TextView energiaTxt = (TextView) findViewById(R.id.detalle_energia_txt);
		
		float distanciaKm = actividad.getDistancia() / 1000.0f;
		float velocidad = mSportumUtil.calcularVelocidadKmh(actividad.getDistancia(), actividad.getTiempo());
		
		actividadImg.setImageDrawable(mRes.getDrawable(mRes.getIdentifier(
				"ic_" + actividad.getTipoActividad(),
				"drawable",
				getPackageName())));
		actividadTxt.setText(mRes.getString(mRes.getIdentifier(
				actividad.getTipoActividad(),
				"string",
				getPackageName())));
		fechaTxt.setText(df.format(actividad.getFecha()));
		tiempoTxt.setText(mSportumUtil.obtenerTiempoTxt(actividad.getTiempo()));
		distanciaTxt.setText(mSportumUtil.obtenerDistanciaTxt(distanciaKm));
		velocidadTxt.setText(mSportumUtil.obtenerVelocidadTxt(velocidad));
		vueltasTxt.setText(String.valueOf(actividad.getNumVueltas()));
		energiaTxt.setText(mSportumUtil.obtenerEnergiaTxt(actividad.getEnergia()));
	}
	
	/**
	 * Crea el diálogo que se muestra al usuario para que confirme que realmente
	 * desea borrar la actividad.
	 * 
	 * @param id Identificador de la actividad en BD
	 * 
	 * @return El dialogo creado
	 */
	private Dialog crearDialogoConfirmarBorrado(int id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(mRes.getString(R.string.titulo_confirmar_borrado));
		builder.setMessage(mRes.getString(R.string.confirmar_borrado));
		builder.setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				borrarActividad();
			}
		});
		builder.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		return builder.create();
	}
	
	/**
	 * Elimina toda la información de la actividad, tanto la almacenada en BD como
	 * la que existe en el sistema de ficheros.
	 */
	private void borrarActividad() {
		if (mSportumDB.borrar(mId)) {
			mSportumFileUtil.borrarArchivoXML(mId);
			Toast.makeText(getApplicationContext(), mRes.getString(R.string.borrado_correcto), Toast.LENGTH_SHORT).show();
			finish();
		}
		else {
			Toast.makeText(getApplicationContext(), mRes.getString(R.string.error_borrado), Toast.LENGTH_SHORT).show();
		}
	}
}
