package com.rlo.sportum.mapa;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Permite dibujar una línea sobre un mapa entre dos puntos dados.
 * 
 * @author rafa
 *
 */
public class LineaOverlay extends Overlay {

	private GeoPoint point1;
	private GeoPoint point2;
	private int colorLinea;

	public LineaOverlay(GeoPoint point1, GeoPoint point2, int colorLinea) {
		super();
		this.point1 = point1;
		this.point2 = point2;
		this.colorLinea = colorLinea;
	}

	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);
		
		Point scrnPoint1 = new Point();
		Point scrnPoint2 = new Point();
		
		mapView.getProjection().toPixels(point1, scrnPoint1);
		mapView.getProjection().toPixels(point2, scrnPoint2);
		
		Paint paint = new Paint();
		paint.setColor(colorLinea);
		paint.setStrokeWidth(5.0f);
		
		canvas.drawLine(scrnPoint1.x, scrnPoint1.y, scrnPoint2.x, scrnPoint2.y, paint);
	
		return true;
	}

}
