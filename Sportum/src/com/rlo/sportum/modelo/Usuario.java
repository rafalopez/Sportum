package com.rlo.sportum.modelo;

/**
 * Información del usuario que realiza la actividad.
 * 
 * @author rafa
 *
 */
public class Usuario {

	private String nombre;
	private String genero;
	private float peso;
	private int edad;
	
	public Usuario() {
		
	}
	
	public Usuario(String nombre, String genero, float peso, int edad) {
		this.nombre = nombre;
		this.genero = genero;
		this.peso = peso;
		this.edad = edad;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getGenero() {
		return genero;
	}
	public void setGenero(String genero) {
		this.genero = genero;
	}
	public float getPeso() {
		return peso;
	}
	public void setPeso(float peso) {
		this.peso = peso;
	}
	public int getEdad() {
		return edad;
	}
	public void setEdad(int edad) {
		this.edad = edad;
	}
	
}
