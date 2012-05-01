package com.rlo.sportum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

import com.rlo.sportum.modelo.Localizacion;
import com.rlo.sportum.util.AbstractDemoChart;
import com.rlo.sportum.util.SportumFileUtil;
import com.rlo.sportum.util.SportumUtil;

/**
 * Permite generar la gráfica de una actividad a partir de los datos
 * de cada uno de los puntos del recorrido.
 * 
 * @author rafa
 *
 */
public class GraficaActivity extends AbstractDemoChart {
	
	private static final String TAG = GraficaActivity.class.getSimpleName();
	
	private Context mCtx;
	private Resources mRes;
	private long mId;
	private SportumUtil mSportumUtil;
	private SportumFileUtil mSportumFileUtil;
	private List<Localizacion> mLocalizacionLst;
	
	private double[] mAltitudEjeX;
	private double[] mVelocidadEjeX;
	private double[] mAltitudValores;
	private double[] mVelocidadValores;
	private double mDistanciaMin;
	private double mDistanciaMax;
	private double mAltitudMin;
	private double mAltitudMax;
	
	/**
	 * Constructor que recibe todos los datos necesarios para generar posteriormente la gráfica.
	 * 
	 * @param context Contexto de la aplicación.
	 * @param res Recursos comunes de la aplicación.
	 * @param id Identificador de BD de la actividad para la cual se quiere generar la gráfica.
	 */
	public GraficaActivity(Context context, Resources res, long id) {
		mCtx = context;
		mRes = res;
		mId = id;
		mSportumUtil = new SportumUtil();
		mSportumFileUtil = new SportumFileUtil(mCtx);
	}
	
	public String getName() {
		return "Gráfica de la actividad";
	}

	public String getDesc() {
		return "Gráfica con la altitud y la velocidad de la actividad";
	}

	/**
	 * Genera el {@link Intent} con la gráfica.
	 * 
	 * @return {@link Intent} con la gráfica generado o null en caso de que se haya
	 * producido algún error.
	 */
	public Intent execute(Context context) {
		
		Intent intent = null;
		mLocalizacionLst = null;
		try {
			mLocalizacionLst = mSportumFileUtil.leerArchivoXML(mId);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (SAXException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		if (mLocalizacionLst != null && !mLocalizacionLst.isEmpty()) {
			calcularValoresGrafica();
			intent = generarGrafica();
		}
		
		return intent;
	}
	
	/**
	 * Calcula todo los datos necesarios para generar la gráfica.
	 */
	private void calcularValoresGrafica() {
		
		Localizacion localizacion, localizacionAnt;
		
		// Inicializar los valores
		int localizacionLstSize = mLocalizacionLst.size();
		mAltitudEjeX = new double[localizacionLstSize];
		mVelocidadEjeX = new double[localizacionLstSize];
		mAltitudValores = new double[localizacionLstSize];
		mVelocidadValores = new double[localizacionLstSize];
		mDistanciaMin = 0.0;
		mDistanciaMax = 0.0;
		mAltitudMin = 0.0;
		mAltitudMax = 0.0;
		
		
		int n = 1;
		Iterator<Localizacion> locIt = mLocalizacionLst.iterator();
		
		// Primera localización
		localizacion = locIt.next();
		
		mAltitudEjeX[0] = 0.0;
		mVelocidadEjeX[0] = 0.0;
		mAltitudValores[0] = localizacion.getAltitud();
		mVelocidadValores[0] = 0.0;
		mAltitudMin = localizacion.getAltitud();
		mAltitudMax = localizacion.getAltitud();
		
		
		// Iterar sobre el resto de localizaciones
		while (locIt.hasNext()) {
			localizacionAnt = localizacion;
			localizacion = locIt.next();
			
			mAltitudEjeX[n] = localizacion.getDistancia() / 1000.0; // En Km.
			mVelocidadEjeX[n] = localizacion.getDistancia() / 1000.0; // En Km.
			mAltitudValores[n] = localizacion.getAltitud();
			mVelocidadValores[n] = mSportumUtil.calcularVelocidadKmh(
					localizacion.getDistancia() - localizacionAnt.getDistancia(),
					localizacion.getTiempo() - localizacionAnt.getTiempo());
			
			mAltitudMin = Math.min(mAltitudMin, localizacion.getAltitud());
			mAltitudMax = Math.max(mAltitudMax, localizacion.getAltitud());
			
			n++;
		}
		
		mDistanciaMax = localizacion.getDistancia() / 1000.0; // En Km.
	}
	
	/**
	 * Usa los datos ya calculados para renderizar un gráfico de lineas X-Y
	 * 
	 * @return {@link Intent} con la gráfica generada
	 */
	private Intent generarGrafica() {
		
		Intent intent;
		
		List<double[]> ejeX = new ArrayList<double[]>();
		ejeX.add(mAltitudEjeX);
		ejeX.add(mVelocidadEjeX);
		List<double[]> values = new ArrayList<double[]>();
		values.add(mAltitudValores);
		
		// Definir el estilo de las líneas de la gráfica
		int[] colors = new int[] { Color.MAGENTA, Color.CYAN };
		PointStyle[] styles = new PointStyle[] { PointStyle.SQUARE, PointStyle.CIRCLE };
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(2);
		setRenderer(renderer, colors, styles);
		XYSeriesRenderer xyRenderer1 = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
		xyRenderer1.setFillPoints(true);
		xyRenderer1.setFillBelowLine(true);
		xyRenderer1.setFillBelowLineColor(Color.MAGENTA);
		xyRenderer1.setLineWidth(4.0f);
		XYSeriesRenderer xyRenderer2 = (XYSeriesRenderer) renderer.getSeriesRendererAt(1);
		xyRenderer2.setFillPoints(true);
		xyRenderer2.setLineWidth(4.0f);
		
		// Definir el resto de estilos de la gráfica
		setChartSettings(
				renderer,
				mRes.getString(R.string.titulo_grafica),
				mRes.getString(R.string.titulo_eje_distancia),
				mRes.getString(R.string.titulo_eje_altitud),
				mDistanciaMin, mDistanciaMax, mAltitudMin, mAltitudMax,
				Color.LTGRAY, Color.LTGRAY);
		renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setShowGrid(false);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setZoomButtonsVisible(true);
		renderer.setPanLimits(new double[] { mDistanciaMin, mDistanciaMax, mAltitudMin, mAltitudMax });
		renderer.setZoomLimits(new double[] { mDistanciaMin, mDistanciaMax, mAltitudMin, mAltitudMax });
		renderer.setZoomEnabled(true, false);
		renderer.setYTitle(mRes.getString(R.string.titulo_eje_velocidad), 1);
		renderer.setYAxisAlign(Align.RIGHT, 1);
		renderer.setYLabelsAlign(Align.LEFT, 1);
		
		// Establecer los valores de la gráfica
		String[] titulos = new String[] { mRes.getString(R.string.leyenda_altitud) };
		XYMultipleSeriesDataset dataset = buildDataset(titulos, ejeX, values);
		values.clear();
		values.add(mVelocidadValores);
		addXYSeries(dataset, new String[] { mRes.getString(R.string.leyenda_velocidad) }, ejeX, values, 1);
		
		// Crear el Intent con la gráfica
		intent = ChartFactory.getLineChartIntent(mCtx, dataset, renderer, mRes.getString(R.string.titulo_grafica));
		
		return intent;
	}

}
