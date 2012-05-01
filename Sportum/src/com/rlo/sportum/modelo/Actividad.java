package com.rlo.sportum.modelo;

import java.util.Date;

/**
 * Representa un registro de la tabla ACTIVIDAD de la BD.
 * 
 * @author rafa
 *
 */
public class Actividad {

	private String tipoActividad;
	private Date fecha;
	private String nombreUsuario;
	private long tiempo;
	private float distancia;
	private int numVueltas;
	private float energia;

	public Actividad() {
		fecha = new Date();
		nombreUsuario = "";
		tiempo = 0;
		distancia = 0.0f;
		numVueltas = 0;
		energia = 0.0f;
	}
	
	public Actividad(String tipoActividad) {
		this.tipoActividad = tipoActividad;
		fecha = new Date();
		nombreUsuario = "";
		tiempo = 0;
		distancia = 0.0f;
		numVueltas = 0;
		energia = 0.0f;
	}
	
	public Actividad(String tipoActividad, Date fecha, String nombreUsuario, long tiempo, float distancia, int numVueltas, float energia) {
		this.tipoActividad = tipoActividad;
		this.fecha = fecha;
		this.nombreUsuario = nombreUsuario;
		this.tiempo = tiempo;
		this.distancia = distancia;
		this.numVueltas = numVueltas;
		this.energia = energia;
	}

	public String getTipoActividad() {
		return tipoActividad;
	}
	
	public void setTipoActividad(String tipoActividad) {
		this.tipoActividad = tipoActividad;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
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

	public int getNumVueltas() {
		return numVueltas;
	}

	public void setNumVueltas(int numVueltas) {
		this.numVueltas = numVueltas;
	}

	public float getEnergia() {
		return energia;
	}

	public void setEnergia(float energia) {
		this.energia = energia;
	}
	
	public float getVelocidad() {
		return distancia / (float)tiempo;
	}
	
}
