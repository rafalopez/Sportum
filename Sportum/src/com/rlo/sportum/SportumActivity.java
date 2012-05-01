package com.rlo.sportum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.rlo.sportum.db.SportumDB;
import com.rlo.sportum.mapa.BanderaIniOverlay;
import com.rlo.sportum.mapa.LineaOverlay;
import com.rlo.sportum.mapa.PosicionActualOverlay;
import com.rlo.sportum.modelo.Actividad;
import com.rlo.sportum.modelo.InfoAudio;
import com.rlo.sportum.modelo.Localizacion;
import com.rlo.sportum.modelo.Objetivo;
import com.rlo.sportum.modelo.Usuario;
import com.rlo.sportum.pager.SportumPagerAdapter;
import com.rlo.sportum.pager.SportumViewPager;
import com.rlo.sportum.util.SportumFileUtil;
import com.rlo.sportum.util.SportumPrefUtil;
import com.rlo.sportum.util.SportumUtil;

/**
 * Clase principal de la aplicación.<br />
 * Se compone de dos pantallas: la pantalla de contadores y la del mapa.<br />
 * La actividad controla ambas vistas y permite su intercambio mediante un {@link ViewPager}<br />
 * 
 * @author rafa
 *
 */
public class SportumActivity extends MapActivity implements LocationListener, OnInitListener {
	
	private static final String TAG = SportumActivity.class.getSimpleName();
	
	private static final int REQ_TTS_STATUS_CHECK = 0;
	
	private static final int[] colores = {Color.MAGENTA, Color.YELLOW, Color.GREEN, Color.CYAN, Color.RED, Color.DKGRAY, Color.WHITE, Color.BLUE};
	
	// Recursos
	private SportumDB sportumDB;
	private SportumUtil sportumUtil;
	private SportumFileUtil sportumFileUtil;
	private SportumPrefUtil sportumPrefUtil;
	private Resources res;
	private SharedPreferences preferencias;
	private OnSharedPreferenceChangeListener preferenciasListener;
	private Timer timer;
	private TimerTask timerTask;
	private Handler handler;
	private Runnable ejecutarActualizarTiempo;
	private LocationManager locationManager;
	private TextToSpeech mTts;
	private boolean realizandoActividad;
	
	// Layouts
	private SportumViewPager pager;
	private SportumPagerAdapter adapter;
	private View contadoresLayout;
	private View mapaLayout;
	
	// Vistas de la pantalla de Contadores
	private Button irAMapaBtn;
	private TextView usuarioNombreTxt;
	private ImageView actividadImg;
	private ImageView objetivoImg;
	private ImageView infoAudioImg;
	private ImageView gpsImg;
	private TextView contadorTiempoTxt;
	private TextView contadorDistanciaTxt;
	private TextView contadorVelocidadTxt;
	private TextView contadorVueltasTxt;
	private TextView contadorEnergiaTxt;
	private Button comenzarBtn;
	private Button terminarBtn;
	
	// Vistas de la pantalla del Mapa
	private MapView mapa;
	private MapController controladorMapa;
	private List<Overlay> capasMapaLst;
	private PosicionActualOverlay posicionActual;
	private Button irAContadoresBtn;
	private TextView contadorTiempoMapaTxt;
	private TextView contadorDistanciaMapaTxt;
	private TextView contadorVelocidadMapaTxt;
	private ImageButton candadoImgBtn;
	private ImageButton sateliteImgBtn;
	private ZoomControls controlesZoom;
	private int colorIdx;
	
	// Datos
	private Usuario usuario;
	private Actividad actividad;
	private Objetivo objetivo;
	private InfoAudio infoAudio;
	
	private long tiempo;
	private float distancia;
	private float velocidad;
	private int nVueltas;
	private float energia;
	
	private long tiempoAnt;
	
	private long infoTiempoAnt;
	private float infoDistanciaAnt;
	private int infoNumVueltasAnt;
	private float infoEnergiaAnt;
	
	private List<Localizacion> localizacionLst;
	private boolean comienzoVuelta;
	private Location localizacionComienzo;
	private Location localizacionAnt;
	private GeoPoint posicionAnt;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Log.d(TAG, "Creando SportumActivity");
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        inicializarRecursos();
        inicializarVistas();
        inicializarDatos();
    }
    
    @Override
    protected void onDestroy() {
    	
    	// Detener el listener sobre los cambios en las preferencias
    	if (preferencias != null) {
    		preferencias.unregisterOnSharedPreferenceChangeListener(preferenciasListener);
    	}
    	
    	// Para el timer
    	if (timer != null) {
    		timer.cancel();
    	}
    	
    	// Detener actualizaciones de localización
    	locationManager.removeUpdates(this);
    	
    	// Cerrar la BD
    	if (sportumDB != null) {
    		sportumDB.cerrar();
    	}
    	
    	// Apagar el TextToSpeech
    	if (mTts != null) {
    		mTts.shutdown();
    	}
    	
    	super.onDestroy();
    }
    
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	
    	MenuItem actividadesRealizadasMenuItem = menu.findItem(R.id.actividades_realizadas);
    	MenuItem configMenuItem = menu.findItem(R.id.config);
    	if (realizandoActividad) {
    		// Si se está realizando la actividad no se permite modificar las preferencias
    		if (actividadesRealizadasMenuItem.isEnabled()) {
    			actividadesRealizadasMenuItem.setEnabled(false);
    		}
	    	if (configMenuItem.isEnabled()) {
	    		configMenuItem.setEnabled(false);
	    	}
    	}
    	else {
    		if (!actividadesRealizadasMenuItem.isEnabled()) {
    			actividadesRealizadasMenuItem.setEnabled(true);
    		}
	    	if (!configMenuItem.isEnabled()) {
	    		configMenuItem.setEnabled(true);
	    	}
    	}
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
	    case R.id.actividades_realizadas:
	    	startActivity(new Intent(this, ListadoActivity.class));
	        return true;
	    case R.id.config:
	    	startActivity(new Intent(this, SportumPreferenceActivity.class));
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (realizandoActividad) {
			actualizarLocalizacion(location);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			gpsImg.setImageDrawable(res.getDrawable(R.drawable.ic_gps_desactivado));
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			gpsImg.setImageDrawable(res.getDrawable(R.drawable.ic_gps_activado));
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == REQ_TTS_STATUS_CHECK) {
			// Respuesta a la comprobación de si el Text-To-Speech está instalado
			switch (resultCode) {
			case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
				// TTS esta istalado, se puede instanciar un objeto de dicha clase
				mTts = new TextToSpeech(this, this);
				Log.d(TAG, "Pico instalado correctamente");
				break;
			case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
			case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
			case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
				// missing data, install it
				Log.d(TAG, "Need language stuff: " + resultCode);
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
				break;
			case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
			default:
				Log.e(TAG, "Got a failure. TTS not available");
			}
		}
	}
	
	/**
	 * Inicializa varias utilidades que necesitará la activity posteriormente como
	 * el acceso a la BD, los recursos de la aplicación, el sistema de Text-To-Speech, etc.
	 */
	private void inicializarRecursos() {
		
		// Gestor de BD
		sportumDB = new SportumDB(this);
		sportumDB.abrir();
		
		// Varias utilidades
		sportumUtil = new SportumUtil();
		
		// Utilidades para ficheros
		sportumFileUtil = new SportumFileUtil(this);
		
		// Recursos de la aplicación
		res = getResources();
		
		// Preferencias de la aplicación
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        preferenciasListener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				sportumPrefUtil.actualizarDatos(key);
			}
		};
        preferencias.registerOnSharedPreferenceChangeListener(preferenciasListener);
        
        
        // Timer para controlar el tiempo de ejecución de la actividad
        handler = new Handler();
		ejecutarActualizarTiempo = new Runnable() {
			public void run() {
				actualizarTiempo();
			}
		};
		timer = null;
        
        // LocationManager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        // Registrar actualizaciones de localización
        locationManager.requestLocationUpdates(
        		LocationManager.GPS_PROVIDER,
        		res.getInteger(R.integer.min_tiempo_actualizacion_gps),
        		res.getInteger(R.integer.min_distancia_actualizacion_gps),
        		this);
        
        // Text To Speech, iniciar comprobación de si está instalado
        mTts = null;
        Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);
        
        realizandoActividad = false;
        
	}
	
	/**
	 * Obtiene la referencia a las vistas de los 2 layouts de la aplicación y define
	 * varios listener sobre alguna de esas vistas.
	 */
	private void inicializarVistas() {
		
		// Layouts
		ArrayList<View> layouts = new ArrayList<View>();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contadoresLayout = inflater.inflate(R.layout.contadores, null);
        mapaLayout = inflater.inflate(R.layout.mapa, null);
        layouts.add(contadoresLayout);
        layouts.add(mapaLayout);
        
        // Pager adapter
        adapter = new SportumPagerAdapter(layouts);
        pager = (SportumViewPager) findViewById(R.id.pager_adapter);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        
        // Mapa
        mapa = (MapView) mapaLayout.findViewById(R.id.mapa);
        mapa.setBuiltInZoomControls(false);
        mapa.setClickable(false);
        mapa.setSatellite(false);
        
        // Controlador del mapa
        controladorMapa = mapa.getController();
        capasMapaLst = mapa.getOverlays();
        controladorMapa.setZoom(15);
        posicionActual = null;
        
        
        // Vistas de la pantalla de Contadores
        irAMapaBtn = (Button) contadoresLayout.findViewById(R.id.ir_a_mapa_btn);
        irAMapaBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pager.isPagingEnabled()) {
					pager.setCurrentItem(1);
				}
			}
		});
        
        usuarioNombreTxt = (TextView) contadoresLayout.findViewById(R.id.usuario_nombre_txt);
        actividadImg = (ImageView) contadoresLayout.findViewById(R.id.actividad_img);
        objetivoImg = (ImageView) contadoresLayout.findViewById(R.id.objetivo_img);
        infoAudioImg = (ImageView) contadoresLayout.findViewById(R.id.info_audio_img);
        gpsImg = (ImageView) contadoresLayout.findViewById(R.id.gps_img);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        	gpsImg.setImageDrawable(res.getDrawable(R.drawable.ic_gps_activado));
        }
        else {
        	gpsImg.setImageDrawable(res.getDrawable(R.drawable.ic_gps_desactivado));
        }
        
        contadorTiempoTxt = (TextView) contadoresLayout.findViewById(R.id.contador_tiempo_txt);
        contadorDistanciaTxt = (TextView) contadoresLayout.findViewById(R.id.contador_distancia_txt);
        contadorVelocidadTxt = (TextView) contadoresLayout.findViewById(R.id.contador_velocidad_txt);
        contadorVueltasTxt = (TextView) contadoresLayout.findViewById(R.id.contador_vueltas_txt);
        contadorEnergiaTxt = (TextView) contadoresLayout.findViewById(R.id.contador_energia_txt);
        
        comenzarBtn = (Button) contadoresLayout.findViewById(R.id.btn_comenzar);
        comenzarBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				comenzar();
			}
		});
        terminarBtn = (Button) contadoresLayout.findViewById(R.id.btn_terminar);
        terminarBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				terminar();
			}
		});
        
        
        // Vistas de la pantalla del Mapa
        irAContadoresBtn = (Button) mapaLayout.findViewById(R.id.ir_a_contadores_btn);
        irAContadoresBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pager.isPagingEnabled()) {
					pager.setCurrentItem(0);
				}
			}
		});
        
        contadorTiempoMapaTxt = (TextView) mapaLayout.findViewById(R.id.contador_tiempo_mapa_txt);
        contadorDistanciaMapaTxt = (TextView) mapaLayout.findViewById(R.id.contador_distancia_mapa_txt);
        contadorVelocidadMapaTxt = (TextView) mapaLayout.findViewById(R.id.contador_velocidad_mapa_txt);
        
        candadoImgBtn = (ImageButton) mapaLayout.findViewById(R.id.candado_img_btn);
        candadoImgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pager.isPagingEnabled()) {
					pager.setPagingEnabled(false);
					mapa.setClickable(true);
					controlesZoom.setVisibility(View.VISIBLE);
					candadoImgBtn.setImageResource(R.drawable.ic_candado_abierto);
				}
				else {
					pager.setPagingEnabled(true);
					mapa.setClickable(false);
					controlesZoom.setVisibility(View.INVISIBLE);
					candadoImgBtn.setImageResource(R.drawable.ic_candado_cerrado);
				}
			}
		});
        
        sateliteImgBtn = (ImageButton) mapaLayout.findViewById(R.id.satelite_img_btn);
        sateliteImgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mapa.isSatellite()) {
					mapa.setSatellite(false);
					sateliteImgBtn.setImageResource(R.drawable.ic_satelite_desactivado);
				}
				else {
					mapa.setSatellite(true);
					sateliteImgBtn.setImageResource(R.drawable.ic_satelite_activado);
				}
			}
		});
        
        controlesZoom = (ZoomControls) mapaLayout.findViewById(R.id.controles_zoom);
        controlesZoom.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				controladorMapa.zoomIn();
			}
		});
        controlesZoom.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				controladorMapa.zoomOut();
			}
		});
        controlesZoom.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * Inicializa la información del usuario, el tipo de actividad, objetivo e información por audio.<br />
	 * También todo lo relativo a las {@link SharedPreferences}.
	 */
	private void inicializarDatos() {
		usuario = new Usuario();
		actividad = new Actividad();
		objetivo = new Objetivo();
		infoAudio = new InfoAudio();
		
		// Utilidades para manejar las preferencias
		sportumPrefUtil = new SportumPrefUtil(this, this, res, preferencias, sportumUtil,
				actividad, usuario, objetivo, infoAudio, usuarioNombreTxt, actividadImg, objetivoImg, infoAudioImg);
		
		sportumPrefUtil.inicializarPreferencias();
	}
	
	/**
	 * Método que se ejecuta cada vez que se obtiene una nueva localización GPS.<br />
	 * A partir de dicha localización, recalcula los datos de:
	 * <ul>
	 * <li>Distancia recorrida</li>
	 * <li>Velocidad actual</li>
	 * <li>Número de vueltas</li>
	 * <li>Energía consumida</li>
	 * </ul>
	 * 
	 * @param localizacion La nueva {@link Location} obtenida del satélite GPS
	 */
	private void actualizarLocalizacion(Location localizacion) {
		GeoPoint posicion = new GeoPoint((int) (localizacion.getLatitude() * 1E6), (int) (localizacion.getLongitude() * 1E6));
		
		if (localizacionAnt == null) {
			// Es la primera localización que se obtiene en esta actividad
			localizacionComienzo = localizacion;
			capasMapaLst.add(new BanderaIniOverlay(posicion, res));
			posicionActual = new PosicionActualOverlay(posicion, res);
			capasMapaLst.add(posicionActual);
		}
		else {
			// Para la segunda localización y siguientes
			float distanciaTramo = localizacionAnt.distanceTo(localizacion);
			long tiempoTramo = tiempo - tiempoAnt;

			// 1. Se actualizan los calculos
			actualizarDistancia(distanciaTramo);
			actualizarVelocidad(tiempoTramo, distanciaTramo);
			actualizarNumeroVueltas(localizacion);
			actualizarEnergia(tiempoTramo);
			actualizarMapa(posicion);

			// 2. Se comprueba si se ha alcanzado el objetivo
			if (objetivoAlcanzado()) {
				terminar();
			}
			// 3. Comprobar si hay que reproducir la información por audio
			if (infoAudioActivado()) {
				reproducirInfoAudio();
			}
		}
		
		localizacionAnt = localizacion;
		posicionAnt = posicion;
		tiempoAnt = tiempo;
		
		localizacionLst.add(new Localizacion(localizacion.getLongitude(), localizacion.getLatitude(), localizacion.getAltitude(), tiempo, distancia));
		
		controladorMapa.animateTo(posicion);
		mapa.invalidate();
	}
	
	/**
	 * Incrementa en uno el número de segundos transcurridos de la actividad
	 */
	private void actualizarTiempo() {
		if (realizandoActividad) {
	    	tiempo++;
	    	
	    	String tiempoTxt = sportumUtil.obtenerTiempoTxt(tiempo);
			contadorTiempoTxt.setText(tiempoTxt);
			contadorTiempoMapaTxt.setText(tiempoTxt);
			
			// Aquí también se comprueba si se ha alcanzado el objetivo...
			if (objetivoAlcanzado()) {
				terminar();
			}
			// Y si se debe reproducir la información por audio
			if (infoAudioActivado()) {
				reproducirInfoAudio();
			}
		}
    }
	
	/**
	 * Incrementa la distancia total recorrida.
	 * 
	 * @param distanciaTramo Distancia en metros del último tramo.
	 */
	private void actualizarDistancia(float distanciaTramo) {
		distancia += distanciaTramo;
		
		String distanciaTxt = sportumUtil.obtenerDistanciaTxt(distancia / 1000.0f);
		contadorDistanciaTxt.setText(distanciaTxt);
		contadorDistanciaMapaTxt.setText(distanciaTxt);
	}
	
	/**
	 * Actualiza el valor de la velocidad del último tramo.
	 * 
	 * @param tiempoTramo Tiempo empleado en el último tramo en segundos.
	 * @param distanciaTramo Distancia recorrida en el último tramo en metros.
	 */
	private void actualizarVelocidad(long tiempoTramo, float distanciaTramo) {
		velocidad = sportumUtil.calcularVelocidadKmh(distanciaTramo, tiempoTramo);
		
		String velocidadTxt = sportumUtil.obtenerVelocidadTxt(velocidad);
		contadorVelocidadTxt.setText(velocidadTxt);
		contadorVelocidadMapaTxt.setText(velocidadTxt);
	}
	
	/**
	 * Comprueba si se ha completado una vuelta, es decir, si se está pasando por el punto de partida y,
	 * en caso de que así sea, se incrementa en 1 el contador de vueltas.
	 * 
	 * @param localizacionAct {@link Location} en la que se encuentra actualmente.
	 */
	private void actualizarNumeroVueltas(Location localizacionAct) {
		
		if (comienzoVuelta) { // No estamos alejando del punto de partida
			if (localizacionComienzo.distanceTo(localizacionAct) > res.getInteger(R.integer.radio_salida_vuelta)) {
				comienzoVuelta = false;
			}
		}
		else { // Nos estamos dirigiendo al punto de partida
			if (localizacionComienzo.distanceTo(localizacionAct) < res.getInteger(R.integer.radio_entrada_vuelta)) {
				// Estamos cerca del punto de partida, actualizar num. vueltas
				nVueltas++;
				comienzoVuelta = true;
				colorIdx = (colorIdx + 1) % colores.length;
				
				contadorVueltasTxt.setText(String.valueOf(nVueltas));
			}
		}
	}
	
	/**
	 * Calcula las calorias consumidas según una fórmula que depende de:
	 * <ul>
	 * <li>El peso del usuario</li>
	 * <li>El tipo de actividad</li>
	 * <li>El tiempo de realización de dicha actividad</li>
	 * <li>El género del usuario</li>
	 * </ul>
	 * 
	 * @param tiempoTramo Tiempo en segundos para el cual se recalcula la energia
	 */
	private void actualizarEnergia(long tiempoTramo) {
		// Gasto calórico (en Kcal) = peso * cosumo_actividad * (tiempo / 60.0) (- 10% si es mujer)
    	int gastoCaloricoInt = res.getInteger(res.getIdentifier(
    			"gasto_calorico_" + actividad.getTipoActividad(),
    			"integer",
    			getPackageName()));
    	float gastoCalorico = (float)gastoCaloricoInt / 1000.0f;
    	
    	float energiaTramo = usuario.getPeso() * gastoCalorico * (tiempoTramo / 60.0f);
    	if (usuario.getGenero().equals(res.getString(R.string.genero_femenino))) {
    		energiaTramo = energiaTramo * 0.1f;
    	}
    	energia += energiaTramo;
    	
    	contadorEnergiaTxt.setText(sportumUtil.obtenerEnergiaTxt(energia));
	}
	
	/**
	 * Dibuja la línea del último tramo recorrido y actualiza el indicador de la
	 * posición actual.
	 * 
	 * @param posicion Posición actual
	 */
	private void actualizarMapa(GeoPoint posicion) {
		capasMapaLst.add(capasMapaLst.size() - 2, new LineaOverlay(posicionAnt, posicion, colores[colorIdx]));
		posicionActual.setPosicionActual(posicion);
	}
	
	/**
	 * Indica si se ha alcanzado el objetivo establecido en las preferencias, en caso
	 * de haberse indicado alguno.
	 * 
	 * @return true si se ha alcanzado el objetivo, false en caso contrario.
	 */
	private boolean objetivoAlcanzado() {
		
		boolean objetivoAlcanzado = false;
		
		if (objetivo.getTipoObjetivo().equals(res.getString(R.string.objetivo_tiempo_key))) {
			if (tiempo >= objetivo.getTiempo()) {
				objetivoAlcanzado = true;
			}
		}
		else if (objetivo.getTipoObjetivo().equals(res.getString(R.string.objetivo_distancia_key))) {
			if (distancia >= objetivo.getDistancia()) {
				objetivoAlcanzado = true;
			}
		}
		else if (objetivo.getTipoObjetivo().equals(res.getString(R.string.objetivo_vueltas_key))) {
			if (nVueltas >= objetivo.getVueltas()) {
				objetivoAlcanzado = true;
			}
		}
		else if (objetivo.getTipoObjetivo().equals(res.getString(R.string.objetivo_energia_key))) {
			if (energia >= objetivo.getEnergia()) {
				objetivoAlcanzado = true;
			}
		}
		
		return objetivoAlcanzado;
	}
	
	/**
	 * Indica si se debe reproducir la información de los datos actuales de la actividad
	 * que se está realizando o no.
	 * 
	 * @return true si se debe reproducir la información por audio, false en caso contrario.
	 */
	private boolean infoAudioActivado() {
		
		boolean infoAudioActivado = false;
		
		if (mTts != null) {
			if (infoAudio.getTipoFrecuencia().equals(res.getString(R.string.frecuencia_tiempo_key))) {
				long tiempoTranscurrido = tiempo - infoTiempoAnt;
				if (tiempoTranscurrido >= infoAudio.getFrecuenciaTiempo()) {
					infoTiempoAnt = tiempo;
					infoAudioActivado = true;
				}
			}
			else if (infoAudio.getTipoFrecuencia().equals(res.getString(R.string.frecuencia_distancia_key))) {
				float distanciaTranscurrida = distancia - infoDistanciaAnt;
				if (distanciaTranscurrida >= infoAudio.getFrecuenciaDistancia()) {
					infoDistanciaAnt = distancia;
					infoAudioActivado = true;
				}
			}
			else if (infoAudio.getTipoFrecuencia().equals(res.getString(R.string.frecuencia_vueltas_key))) {
				int numVueltasTrascurridas = nVueltas - infoNumVueltasAnt;
				if (numVueltasTrascurridas >= infoAudio.getFrecuenciaVueltas()) {
					infoNumVueltasAnt = nVueltas;
					infoAudioActivado = true;
				}
			}
			else if (infoAudio.getTipoFrecuencia().equals(res.getString(R.string.frecuencia_energia_key))) {
				float energiaConsumida = energia - infoEnergiaAnt;
				if (energiaConsumida >= infoAudio.getFrecuenciaEnergia()) {
					infoEnergiaAnt = energia;
					infoAudioActivado = true;
				}
			}
		}
		
		return infoAudioActivado;
	}

	/**
	 * Reproduce mediante Texto-To-Speech la información que se ha marcado en las preferencias.
	 */
	private void reproducirInfoAudio() {
		if (infoAudio.isInfoTiempo()) {
			mTts.speak(
					res.getString(
							R.string.tts_info_tiempo,
							sportumUtil.obtenerHoras(tiempo),
							sportumUtil.obtenerMinutos(tiempo),
							sportumUtil.obtenerSegundos(tiempo)),
					TextToSpeech.QUEUE_ADD, null);
		}
		if (infoAudio.isInfoDistancia()) {
			mTts.speak(
					res.getString(
							R.string.tts_info_distancia,
							sportumUtil.obtenerKilometros(distancia),
							sportumUtil.obtenerMetros(distancia)),
					TextToSpeech.QUEUE_ADD, null);
		}
		if (infoAudio.isInfoVueltas()) {
			mTts.speak(res.getString(R.string.tts_info_vueltas, nVueltas), TextToSpeech.QUEUE_ADD, null);
		}
		if (infoAudio.isInfoEnergia()) {
			mTts.speak(res.getString(R.string.tts_info_energia, (int) energia), TextToSpeech.QUEUE_ADD, null);
		}
	}
	
	/**
	 * Ejecuta las acciones necesarias para el comienzo de una actividad.
	 */
	private void comenzar() {
		
		realizandoActividad = true;
		
		// Fecha de comienzo de la actividad
		actividad.setFecha(new Date());
		
		// Inicializar contadores
		tiempo = 0L;
        distancia = 0.0f;
        velocidad = 0.0f;
        nVueltas = 0;
        energia = 0.0f;
        
        tiempoAnt = 0L;
        
        infoTiempoAnt = 0L;
        infoDistanciaAnt = 0.0f;
        infoNumVueltasAnt = 0;
        infoEnergiaAnt = 0.0f;
        
        comienzoVuelta = true;
        colorIdx = 0;
        
        // Inicializar lista de localizaciones
        localizacionLst = new ArrayList<Localizacion>();
        localizacionAnt = null;
        
		// Comenzar tiempo
        timerTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(ejecutarActualizarTiempo);
			}
		};
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 1000); // Ejecutar cada 1000 ms
        
        // Mostrar botón Terminar
		comenzarBtn.setVisibility(View.GONE);
		terminarBtn.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Finaliza la actividad, inserta en BD, escribe el recorrido en un fichero y muestra el detalle.
	 */
	private void terminar() {
		
		// Reiniciar flag
		realizandoActividad = false;
		
		// Parar tiempo
		timer.cancel();
		
		// Volver a mostrar el botón de Comenzar
		terminarBtn.setVisibility(View.GONE);
		comenzarBtn.setVisibility(View.VISIBLE);
		
		actividad.setNombreUsuario(usuario.getNombre());
		actividad.setTiempo(tiempo);
		actividad.setDistancia(distancia);
		actividad.setNumVueltas(nVueltas);
		actividad.setEnergia(energia);
		
		// Insertar en BD
		long id = sportumDB.insertar(actividad);
		
		// Crear fichero de trazas GPS
		try {
			sportumFileUtil.guardarArchivoXML(id, localizacionLst);
		} catch (ParserConfigurationException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (TransformerException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		// Reiniciar las dos pantallas prncipales
		reiniciarContadores();
		reiniciarMapa();
		
		// Se muestra la pantalla del detalle
		Intent i = new Intent(this, DetalleActivity.class);
		i.putExtra("id", id);
		startActivity(i);
	}
	
	/**
	 * Vuelve a dejar la pantalla de contadores como al inicio.
	 */
	private void reiniciarContadores() {
		contadorTiempoTxt.setText(res.getString(R.string.contador_tiempo_ini));
		contadorDistanciaTxt.setText(res.getString(R.string.contador_distancia_ini));
		contadorVelocidadTxt.setText(res.getString(R.string.contador_velocidad_ini));
		contadorVueltasTxt.setText(res.getString(R.string.contador_vueltas_ini));
		contadorEnergiaTxt.setText(res.getString(R.string.contador_energia_ini));
		
		contadorTiempoMapaTxt.setText(res.getString(R.string.contador_tiempo_ini));
		contadorDistanciaMapaTxt.setText(res.getString(R.string.contador_distancia_ini));
		contadorVelocidadMapaTxt.setText(res.getString(R.string.contador_velocidad_ini));
	}
	
	/**
	 * Limpia el mapa de los objetos {@link Overlay} que se habían creado.
	 */
	private void reiniciarMapa() {
		capasMapaLst.clear();
		mapa.invalidate();
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			
		}
	}
	
}