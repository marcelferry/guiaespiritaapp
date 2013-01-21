package br.com.mythe.droid.gelib.cursor;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import br.com.mythe.droid.common.util.MapUtils;
import br.com.mythe.droid.common.util.Utils;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.database.objects.Casas;

public class EstadosListaCursor extends SimpleCursorAdapter {

	Location origem;
	
	public EstadosListaCursor(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		origem = MapUtils.getMyLocation( context );
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = super.newView(context, cursor, parent);
		return v;

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);

		TextView texto = (TextView) view.findViewById(R.id.estado);
		
		String sigla = cursor.getString(cursor.getColumnIndex(Casas.ESTADO));
		
		if(sigla.trim().length() <= 2){
			String estado = Utils.getEstado(sigla);
			texto.setText(estado);
		} else {
			texto.setText(sigla);
		}
		
	}

}
