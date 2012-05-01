package com.rlo.sportum.adaptadores;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rlo.sportum.R;
import com.rlo.sportum.db.SportumDB;
import com.rlo.sportum.util.SportumUtil;

/**
 * {@link CursorAdapter} para las filas del listado, para poder mostrar un layout personalizado.
 * 
 * @author rafa
 *
 */
public class ActividadCursorAdapter extends CursorAdapter {

	private Resources res;
	private LayoutInflater mInflater;
	private SportumUtil sportumUtil;
	
	private SimpleDateFormat df;
	
	private int actividadTipoIdx;
	private int actividadFechaIdx;
	private int actividadNombreUsuarioIdx;
	private int actividadTiempoIdx;
	
	public ActividadCursorAdapter(Context context, Cursor c) {
		super(context, c);
		
		res = context.getResources();
		mInflater = LayoutInflater.from(context);
		sportumUtil = new SportumUtil();
		df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		actividadTipoIdx = c.getColumnIndex(SportumDB.C_ACTIVIDAD_TIPO);
		actividadFechaIdx = c.getColumnIndex(SportumDB.C_ACTIVIDAD_FECHA);
		actividadNombreUsuarioIdx = c.getColumnIndex(SportumDB.C_ACTIVIDAD_NOMBRE_USUARIO);
		actividadTiempoIdx = c.getColumnIndex(SportumDB.C_ACTIVIDAD_TIEMPO);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView actividadImg = (ImageView) view.findViewById(R.id.listado_actividad_img);
		TextView nombreUsuarioTxt = (TextView) view.findViewById(R.id.listado_nombre_usuario_txt);
		TextView fechaTxt = (TextView) view.findViewById(R.id.listado_fecha_txt);
		TextView tiempoTxt = (TextView) view.findViewById(R.id.listado_tiempo_txt);
		
		actividadImg.setImageDrawable(res.getDrawable(res.getIdentifier(
				"ic_" + cursor.getString(actividadTipoIdx),
				"drawable",
				context.getPackageName())));
		Date fecha = new Date(cursor.getLong(actividadFechaIdx));
		nombreUsuarioTxt.setText(cursor.getString(actividadNombreUsuarioIdx));
		fechaTxt.setText(df.format(fecha));
		tiempoTxt.setText(sportumUtil.obtenerTiempoTxt(cursor.getLong(actividadTiempoIdx)));
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = mInflater.inflate(R.layout.fila_listado, parent, false);
		return view;
	}

}
