package com.rlo.sportum.db;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rlo.sportum.modelo.Actividad;

/**
 * Manejador de BD de la aplicación Sportum.<br />
 * Permite abrir/cerrar conexiones a la BD, consultar, actualizar e insertar datos.
 * 
 * @author rafa
 *
 */
public class SportumDB {
	
	public static final String NOMBRE_BD = "sportum";
	public static final int VERSION_BD = 1;
	
	public static final String T_ACTIVIDAD = "actividad";
	public static final String C_ACTIVIDAD_ID = "_id";
	public static final String C_ACTIVIDAD_TIPO = "tipo";
	public static final String C_ACTIVIDAD_FECHA = "fecha";
	public static final String C_ACTIVIDAD_NOMBRE_USUARIO = "nombre_usuario";
	public static final String C_ACTIVIDAD_TIEMPO = "tiempo";
	public static final String C_ACTIVIDAD_DISTANCIA = "distancia";
	public static final String C_ACTIVIDAD_VUELTAS = "vueltas";
	public static final String C_ACTIVIDAD_ENERGIA = "energia";
	public static final String[] T_ACTIVIDAD_COLUMNAS = {C_ACTIVIDAD_ID, C_ACTIVIDAD_TIPO, C_ACTIVIDAD_FECHA,
		C_ACTIVIDAD_NOMBRE_USUARIO, C_ACTIVIDAD_TIEMPO, C_ACTIVIDAD_DISTANCIA, C_ACTIVIDAD_VUELTAS, C_ACTIVIDAD_ENERGIA};
	
	private SQLiteDatabase db;
	private final DBOpenHelper dbOpenHelper;

	
	public SportumDB(Context context) {
		dbOpenHelper = new DBOpenHelper(context);
	}
	
	/**
	 * Obtiene una referencia a la BD
	 */
	public void abrir() {
		if (db == null) {
			db = dbOpenHelper.getWritableDatabase();
		}
	}
	
	/**
	 * Cierra la BD
	 */
	public void cerrar() {
		if (db != null) {
			db.close();
			db = null;
		}
	}
	
	/**
	 * Inserta una actividad con los valores que recibe como parámetro.
	 * 
	 * @param actividad La {@link Actividad} que se quiere insertar en BD.
	 * 
	 * @return El nuevo identificador del registro creado o -1 si no se ha podido insertar.
	 */
	public long insertar(Actividad actividad) {
		
		ContentValues valores = new ContentValues();
		valores.put(C_ACTIVIDAD_TIPO, actividad.getTipoActividad());
		valores.put(C_ACTIVIDAD_FECHA, actividad.getFecha().getTime());
		valores.put(C_ACTIVIDAD_NOMBRE_USUARIO, actividad.getNombreUsuario());
		valores.put(C_ACTIVIDAD_TIEMPO, actividad.getTiempo());
		valores.put(C_ACTIVIDAD_DISTANCIA, actividad.getDistancia());
		valores.put(C_ACTIVIDAD_VUELTAS, actividad.getNumVueltas());
		valores.put(C_ACTIVIDAD_ENERGIA, actividad.getEnergia());
		
		return db.insert(T_ACTIVIDAD, null, valores);
	}
	
	/**
	 * Obtiene un registro de la tabla ACTIVIDAD y crea un objeto de tipo {@link Actividad} con los datos.
	 * 
	 * @param id Identificador que se desea consultar
	 * 
	 * @return Objeto de tipo {@link Actividad} o null si se ha producido un error.
	 */
	public Actividad obtenerActividad(long id) {
		Actividad actividad = null;
		
		Cursor c = db.query(
				T_ACTIVIDAD,
				T_ACTIVIDAD_COLUMNAS,
				C_ACTIVIDAD_ID + "=" + id,
				null, null, null, null, null);
		
		if (c != null) {
			c.moveToFirst();
			actividad = new Actividad();
			actividad.setTipoActividad(c.getString(1));
			actividad.setFecha(new Date(c.getLong(2)));
			actividad.setNombreUsuario(c.getString(3));
			actividad.setTiempo(c.getLong(4));
			actividad.setDistancia(c.getFloat(5));
			actividad.setNumVueltas(c.getInt(6));
			actividad.setEnergia(c.getFloat(7));
		}
        
		return actividad;
	}
	
	/**
	 * Obtiene un {@link Cursor} con todos los registros de la tabla ACTIVIDAD
	 * @return
	 */
	public Cursor obtenerTodo() {
		return obtenerTodo(null);
	}
		
	/**
	 * Obtiene un {@link Cursor} con todos los registros de la tabla ACTIVIDAD
	 * ordenados según el crtierio que se le pasa como parámetro.
	 * 
	 * @param orderBy String con el criterio de ordenación
	 * 
	 * @return {@link Cursor} con los registros obtenidos.
	 */
	public Cursor obtenerTodo(String orderBy) {
		return db.query(T_ACTIVIDAD, T_ACTIVIDAD_COLUMNAS, null, null, null, null, orderBy);
	}	
	
	/**
	 * Elimina un registro de la BD que tenga como ID el que se le pasa como parámetro.
	 * 
	 * @param id Identificador del registro que se quiere eliminar.
	 * 
	 * @return true si se ha eliminado, false en caso contrario.
	 */
	public boolean borrar(long id) {
		return db.delete(T_ACTIVIDAD, C_ACTIVIDAD_ID + "=" + id, null) > 0;
	}
	
	
	
	private static class DBOpenHelper extends SQLiteOpenHelper {
		
		public DBOpenHelper(Context context) {
			super(context, NOMBRE_BD, null, VERSION_BD);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + T_ACTIVIDAD + " ( "
					+ C_ACTIVIDAD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ C_ACTIVIDAD_TIPO + " TEXT NOT NULL, "
					+ C_ACTIVIDAD_FECHA + " NUMBER, "
					+ C_ACTIVIDAD_TIEMPO + " INTEGER NOT NULL, "
					+ C_ACTIVIDAD_NOMBRE_USUARIO + " TEXT NOT NULL, "
					+ C_ACTIVIDAD_DISTANCIA + " NUMBER NOT NULL, "
					+ C_ACTIVIDAD_VUELTAS + " INTEGER NOT NULL, "
					+ C_ACTIVIDAD_ENERGIA + " NUMBER NOT NULL)");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + T_ACTIVIDAD);
	        onCreate(db);
		}
	}
	
}
