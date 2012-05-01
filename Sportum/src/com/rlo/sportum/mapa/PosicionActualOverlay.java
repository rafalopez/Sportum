package com.rlo.sportum.mapa;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.rlo.sportum.R;

/**
 * Dibuja un icono sobre un mapa indicando la posición actual en la que se encuentra
 * el dispositivo.
 * 
 * @author rafa
 *
 */
public class PosicionActualOverlay extends Overlay {

	private GeoPoint posicionActual;
	private Resources res;
	
	public PosicionActualOverlay(GeoPoint posicionActual, Resources res) {
		super();
		this.posicionActual = posicionActual;
		this.res = res;
	}
	
	public GeoPoint getPosicionActual() {
		return posicionActual;
	}

	public void setPosicionActual(GeoPoint posicionActual) {
		this.posicionActual = posicionActual;
	}

	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);
		
		Point scrnPoint = new Point();
		mapView.getProjection().toPixels(this.posicionActual, scrnPoint);
		Bitmap marker = BitmapFactory.decodeResource(res, R.drawable.ic_posicion_actual);
		canvas.drawBitmap(marker, scrnPoint.x - marker.getWidth() / 2, scrnPoint.y - marker.getHeight() / 2, null);
		
		return true;
	}
}
