package com.rlo.sportum.modelo;

/**
 * Representa la configuración seleccionado del objetivo de la actividad.
 * 
 * @author rafa
 *
 */
public class Objetivo {

	private String tipoObjetivo;
	private long tiempo;
	private float distancia;
	private int vueltas;
	private float energia;
	
	public Objetivo() {
		
	}
	
	public Objetivo(String tipoObjetivo, long tiempo, float distancia, int vueltas, float energia) {
		this.tipoObjetivo = tipoObjetivo;
		this.tiempo = tiempo;
		this.distancia = distancia;
		this.vueltas = vueltas;
		this.energia = energia;
	}
	public String getTipoObjetivo() {
		return tipoObjetivo;
	}
	public void setTipoObjetivo(String tipoObjetivo) {
		this.tipoObjetivo = tipoObjetivo;
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
	public int getVueltas() {
		return vueltas;
	}
	public void setVueltas(int vueltas) {
		this.vueltas = vueltas;
	}
	public float getEnergia() {
		return energia;
	}
	public void setEnergia(float energia) {
		this.energia = energia;
	}
	
}
