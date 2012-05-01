package com.rlo.sportum;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.rlo.sportum.adaptadores.ActividadCursorAdapter;
import com.rlo.sportum.db.SportumDB;

/**
 * Actividad que muestra el listado de todas las actividades realizadas
 * y permite su ordenación según varios criterios.
 * 
 * @author rafa
 *
 */
public class ListadoActivity extends ListActivity {

	@SuppressWarnings("unused")
	private static final String TAG = ListadoActivity.class.getSimpleName();
	
	private static final String ORDENAR_POR_FECHA = SportumDB.C_ACTIVIDAD_FECHA + " DESC";
	private static final String ORDENAR_POR_TIPO_ACTIVIDAD = SportumDB.C_ACTIVIDAD_TIPO + " ASC";
	private static final String ORDENAR_POR_TIEMPO = SportumDB.C_ACTIVIDAD_TIEMPO + " DESC";
	private static final String ORDENAR_POR_DISTANCIA = SportumDB.C_ACTIVIDAD_DISTANCIA + " DESC";
	
	private SportumDB mSportumDB;
	private ActividadCursorAdapter mActividades;
	private String mOrdenar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listado);
		
		inicializarDatos();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.listado_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case R.id.ordenar_por_fecha:
	    	item.setChecked(true);
	    	mOrdenar = ORDENAR_POR_FECHA;
	    	recalcularDatos(mOrdenar);
	        return true;
	    case R.id.ordenar_por_tipo_actividad:
	    	item.setChecked(true);
	    	mOrdenar = ORDENAR_POR_TIPO_ACTIVIDAD;
	    	recalcularDatos(mOrdenar);
	        return true;
	    case R.id.ordenar_por_tiempo:
	    	item.setChecked(true);
	    	mOrdenar = ORDENAR_POR_TIEMPO;
	    	recalcularDatos(mOrdenar);
	        return true;
	    case R.id.ordenar_por_distancia:
	    	item.setChecked(true);
	    	mOrdenar = ORDENAR_POR_DISTANCIA;
	    	recalcularDatos(mOrdenar);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(this, DetalleActivity.class);
		i.putExtra("id", id);
		startActivity(i);
	}
	
	/**
	 * Consulta la BD y muestra por pantalla el listado de actividades.
	 */
	private void inicializarDatos() {
		mOrdenar = ORDENAR_POR_FECHA;
		
		mSportumDB = new SportumDB(this);
		mSportumDB.abrir();
		
		Cursor c = mSportumDB.obtenerTodo(mOrdenar);
        startManagingCursor(c);

        mActividades = new ActividadCursorAdapter(this, c);
        setListAdapter(mActividades);
	}
	
	/**
	 * Vuelve a consultar la BD según un nuevo criterio de ordenación
	 * 
	 * @param ordenacion El string con el nuevo criterio de ordenación con el formato <b>campo_ordenacion ASC|DESC</b>
	 */
	private void recalcularDatos(String ordenacion) {
		Cursor c = mSportumDB.obtenerTodo(ordenacion);
    	startManagingCursor(c);
    	mActividades.changeCursor(c);
	}
}
