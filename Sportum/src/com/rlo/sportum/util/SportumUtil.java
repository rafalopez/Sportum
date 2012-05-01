package com.rlo.sportum.util;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.view.WindowManager;

public class SportumUtil {

	@SuppressWarnings("unused")
	private static final String TAG = SportumUtil.class.getSimpleName();
	
	
	public void mantenerPantallaEncendida(Activity activity, boolean mantenerPantallaEncendida) {
		if (mantenerPantallaEncendida) {
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			activity.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}
	
	public void pantallaCompleta(Activity activity, boolean pantallaCompleta) {
		if (pantallaCompleta) {
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		else {
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
	
	public int calcularEdad(int anyoNacimiento, int mesNacimiento, int diaNacimiento) {
		int edad;
		
		Calendar cal = Calendar.getInstance();
		int anyoActual = cal.get(Calendar.YEAR);
		int mesActual = cal.get(Calendar.MONTH);
		int diaActual = cal.get(Calendar.DAY_OF_MONTH);
		
		edad = anyoActual - anyoNacimiento;
		if (mesNacimiento > mesActual) {
			edad--;
		} else if (mesActual == mesNacimiento) {
			if (diaNacimiento > diaActual) {
				edad--;
			}
		}
		
		return edad;
	}
	
	public boolean esFechaNacimientoValida(int anyoNacimiento, int mesNacimiento, int diaNacimiento) {
		Calendar cal = Calendar.getInstance();
		Date fechaActual = cal.getTime();
		
		cal.set(anyoNacimiento, mesNacimiento, diaNacimiento);
		Date fechaNacimiento = cal.getTime();
		
		return fechaNacimiento.before(fechaActual);
	}
	
	public float calcularVelocidadKmh(float distancia, long tiempo) {
		float velocidad;
		if (tiempo == 0) {
			velocidad = 0.0f;
		}
		else {
			velocidad = (distancia / tiempo) * 3600.0f / 1000.0f; // km/h
		}
		return velocidad;
	}
	
	public String obtenerTiempoTxt(long tiempo) {
		String tiempoTxt;
		
		long segundos = obtenerSegundos(tiempo);
		long minutos = obtenerMinutos(tiempo);
		long horas   = obtenerHoras(tiempo);
		
		tiempoTxt = String.format("%02d:%02d:%02d", horas, minutos, segundos);
		
		return tiempoTxt;
	}
	
	public String obtenerDistanciaTxt(float distancia) {
		String distanciaTxt;
		
		DecimalFormat df = new DecimalFormat("0.00");
		distanciaTxt = df.format(distancia);
		
		return distanciaTxt;
	}
	
	public String obtenerVelocidadTxt(float velocidad) {
		String velocidadTxt;
		
		DecimalFormat df = new DecimalFormat("0.0");
		velocidadTxt = df.format(velocidad);
		
		return velocidadTxt;
	}
	
	public String obtenerEnergiaTxt(float energia) {
		String energiaTxt;
		
		DecimalFormat df = new DecimalFormat("0");
		energiaTxt = df.format(energia);
		
		return energiaTxt;
	}
	
	public long obtenerSegundos(long tiempo) {
		return tiempo % 60;
	}
	
	public long obtenerMinutos(long tiempo) {
		return ((tiempo / 60) % 60);
	}
	
	public long obtenerHoras(long tiempo) {
		return ((tiempo / (60*60)) % 24);
	}
	
	public int obtenerMetros(float distancia) {
		return (int) (distancia % 1000.0);
	}
	
	public int obtenerKilometros(float distancia) {
		return (int) (distancia / 1000.0);
	}
	
}
