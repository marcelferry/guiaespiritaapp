package br.com.mythe.droid.gelib.cursor;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import br.com.mythe.droid.common.util.MapUtils;
import br.com.mythe.droid.gelib.R;

public class CasasListaCursor extends SimpleCursorAdapter {

	Location origem;
	
	public CasasListaCursor(Context context, int layout, Cursor c,
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

		try {
			
			ImageView imagem = (ImageView) view.findViewById(R.id.tipoImage);
			
			String tipo = cursor.getString(9);
			
			if(tipo != null && tipo.equals("centro")){
				imagem.setImageDrawable(context.getResources().getDrawable(R.drawable.list_icon_entidade));
				imagem.setContentDescription("Entidade");
			} else if(tipo != null && tipo.equals("escola")) {
				imagem.setImageDrawable(context.getResources().getDrawable(R.drawable.list_icon_escola));	
				imagem.setContentDescription("Escola");
			} else if(tipo != null && tipo.equals("livraria")) {
				imagem.setImageDrawable(context.getResources().getDrawable(R.drawable.list_icon_livraria));
				imagem.setContentDescription("Livraria");
			} else if(tipo != null && tipo.equals("cfas")) {
				imagem.setImageDrawable(context.getResources().getDrawable(R.drawable.list_icon_cfas));
				imagem.setContentDescription("CFAS");
			} else if(tipo != null && tipo.equals("assistencia")) {
				imagem.setImageDrawable(context.getResources().getDrawable(R.drawable.list_icon_assistencia));
				imagem.setContentDescription("Assistência");
			} else {
				imagem.setImageDrawable(context.getResources().getDrawable(R.drawable.list_no_image));
			}
			TextView texto = (TextView) view.findViewById(R.id.distancia);
			Double lat = cursor.getDouble(7);
			Double lng = cursor.getDouble(8);

			Location destino = new Location("Centro");
			destino.setLatitude(lat);
			destino.setLongitude(lng);
			
			String novoTexto = MapUtils.getDistanceFormatada(origem, destino);

			texto.setText(novoTexto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
