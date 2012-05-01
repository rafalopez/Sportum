package com.rlo.sportum;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.SAXException;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.rlo.sportum.db.SportumDB;
import com.rlo.sportum.mapa.BanderaFinOverlay;
import com.rlo.sportum.mapa.BanderaIniOverlay;
import com.rlo.sportum.mapa.LineaOverlay;
import com.rlo.sportum.modelo.Localizacion;
import com.rlo.sportum.util.SportumFileUtil;

/**
 * Muestra el mapa con todo el recorrido de una actividad.
 * 
 * @author rafa
 *
 */
public class MapaActivity extends MapActivity {

	private static final String TAG = MapaActivity.class.getSimpleName();
	
	private SportumDB mSportumDB;
	private SportumFileUtil mSportumFileUtil;
	
	private MapView mMapa;
	private MapController mControladorMapa;
	private List<Overlay> mCapasMapaLst;
	
	private long mId;
	private List<Localizacion> mLocalizacionLst;
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		
		Log.d(TAG, "Creando MapaActivity");
		
		super.onCreate(icicle);
		setContentView(R.layout.mapa_recorrido);
		
		inicializarRecursos();
		inicializarVistas();
		inicializarDatos();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * Inicializa el acceso a la BD y otras utilidades.
	 */
	private void inicializarRecursos() {
		
		mSportumDB = new SportumDB(this);
		mSportumDB.abrir();
		
		mSportumFileUtil = new SportumFileUtil(this);
	}
	
	/**
	 * Obtiene la referencia a todos los elementos del mapa.
	 */
	private void inicializarVistas() {
		mMapa = (MapView) findViewById(R.id.mapa_recorrido);
        mMapa.setBuiltInZoomControls(true);
        
        mControladorMapa = mMapa.getController();
        mCapasMapaLst = mMapa.getOverlays();
	}
	
	/**
	 * Obtiene los datos del recorrido e imprime el recorrido en el mapa.
	 */
	private void inicializarDatos() {
		Intent i = getIntent();
		mId = i.getLongExtra("id", 0);
		
		mLocalizacionLst = null;
		try {
			mLocalizacionLst = mSportumFileUtil.leerArchivoXML(mId);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (SAXException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		if (mLocalizacionLst != null && !mLocalizacionLst.isEmpty()) {
			// Se ha obtenido una lista de localizaciones, se muestran sobre el mapa
			rellenarRecorridoMapa();
		}
		else {
			// Se ha producido un error al consultar el fichero o no existe
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_datos), Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	/**
	 * Dibuja sobre el mapa todo el recorrido de la actividad mediante el uso de una
	 * serie de {@link Overlay}
	 */
	private void rellenarRecorridoMapa() {
		
		GeoPoint posicion, posicionAnt, posicionIni, posicionFin;
		Iterator<Localizacion> locIt = mLocalizacionLst.iterator();
		
		// Obtiene la primera posición
		Localizacion localizacion = locIt.next();
		posicion = new GeoPoint((int) (localizacion.getLatitud() * 1E6), (int) (localizacion.getLongitud() * 1E6));
		posicionIni = posicion;
		
		// Para el resto de posiciones va dibujando lineas entre cada par
		while (locIt.hasNext()) {
			localizacion = locIt.next();
			posicionAnt = posicion;
			posicion = new GeoPoint((int) (localizacion.getLatitud() * 1E6), (int) (localizacion.getLongitud() * 1E6));
			mCapasMapaLst.add(new LineaOverlay(posicionAnt, posicion, Color.MAGENTA));
		}
		
		// Dibuja la bandera de inicio de recorrido en la primera posición
		mCapasMapaLst.add(new BanderaIniOverlay(posicionIni, getResources()));
		mControladorMapa.animateTo(posicionIni);
		
		// Finalmente dibuja la bandera de fin de recorrido en la última posición
		posicionFin = posicion;
		mCapasMapaLst.add(new BanderaFinOverlay(posicionFin, getResources()));
		
		mMapa.invalidate();
	}

}
