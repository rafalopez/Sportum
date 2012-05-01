package com.rlo.sportum.modelo;

/**
 * Representa la configuración seleccionada sobre la información de audio.
 * 
 * @author rafa
 *
 */
public class InfoAudio {

	private String tipoFrecuencia;
	private long frecuenciaTiempo;
	private float frecuenciaDistancia;
	private int frecuenciaVueltas;
	private float frecuenciaEnergia;
	private boolean infoDistancia;
	private boolean infoTiempo;
	private boolean infoVueltas;
	private boolean infoEnergia;
	
	public InfoAudio() {
		
	}
	
	public InfoAudio(String tipoFrecuencia, long frecuenciaTiempo,
			float frecuenciaDistancia, int frecuenciaVueltas,
			float frecuenciaEnergia, boolean infoDistancia, boolean infoTiempo,
			boolean infoVueltas, boolean infoEnergia) {
		this.tipoFrecuencia = tipoFrecuencia;
		this.frecuenciaTiempo = frecuenciaTiempo;
		this.frecuenciaDistancia = frecuenciaDistancia;
		this.frecuenciaVueltas = frecuenciaVueltas;
		this.frecuenciaEnergia = frecuenciaEnergia;
		this.infoDistancia = infoDistancia;
		this.infoTiempo = infoTiempo;
		this.infoVueltas = infoVueltas;
		this.infoEnergia = infoEnergia;
	}
	
	public String getTipoFrecuencia() {
		return tipoFrecuencia;
	}
	public void setTipoFrecuencia(String tipoFrecuencia) {
		this.tipoFrecuencia = tipoFrecuencia;
	}
	public long getFrecuenciaTiempo() {
		return frecuenciaTiempo;
	}
	public void setFrecuenciaTiempo(long frecuenciaTiempo) {
		this.frecuenciaTiempo = frecuenciaTiempo;
	}
	public float getFrecuenciaDistancia() {
		return frecuenciaDistancia;
	}
	public void setFrecuenciaDistancia(float frecuenciaDistancia) {
		this.frecuenciaDistancia = frecuenciaDistancia;
	}
	public int getFrecuenciaVueltas() {
		return frecuenciaVueltas;
	}
	public void setFrecuenciaVueltas(int frecuenciaVueltas) {
		this.frecuenciaVueltas = frecuenciaVueltas;
	}
	public float getFrecuenciaEnergia() {
		return frecuenciaEnergia;
	}
	public void setFrecuenciaEnergia(float frecuenciaEnergia) {
		this.frecuenciaEnergia = frecuenciaEnergia;
	}
	public boolean isInfoDistancia() {
		return infoDistancia;
	}
	public void setInfoDistancia(boolean infoDistancia) {
		this.infoDistancia = infoDistancia;
	}
	public boolean isInfoTiempo() {
		return infoTiempo;
	}
	public void setInfoTiempo(boolean infoTiempo) {
		this.infoTiempo = infoTiempo;
	}
	public boolean isInfoVueltas() {
		return infoVueltas;
	}
	public void setInfoVueltas(boolean infoVueltas) {
		this.infoVueltas = infoVueltas;
	}
	public boolean isInfoEnergia() {
		return infoEnergia;
	}
	public void setInfoEnergia(boolean infoEnergia) {
		this.infoEnergia = infoEnergia;
	}
	
}
