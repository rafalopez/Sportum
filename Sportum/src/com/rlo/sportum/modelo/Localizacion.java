package com.rlo.sportum.modelo;

/**
 * Representa un punto del recorrido realizado por el usuario durante la actividad.
 * 
 * @author rafa
 *
 */
public class Localizacion {

	private double longitud;
	private double latitud;
	private double altitud;
	private long tiempo;
	private float distancia;
	
	public Localizacion() {
		longitud = 0.0;
		latitud = 0.0;
		altitud = 0.0;
		tiempo = 0;
		distancia = 0.0f;
	}
	
	public Localizacion(double longitud, double latitud, double altitud, long tiempo, float distancia) {
		this.longitud = longitud;
		this.latitud = latitud;
		this.altitud = altitud;
		this.tiempo = tiempo;
		this.distancia = distancia;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getAltitud() {
		return altitud;
	}

	public void setAltitud(double altitud) {
		this.altitud = altitud;
	}

	public long getTiempo() {
		return tiempo;
	}

	public void setTiempo(long tiempo) {
		this.tiempo = tiempo;
	}

	public float getDistancia() {
		return distancia;
	}

	public void setDistancia(float distancia) {
		this.distancia = distancia;
	}
	
}
