package br.com.mythe.droid.gelib.cursor;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.mythe.droid.common.util.MapUtils;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.database.objects.Casas;

public class ItemListaCursorAdapter extends CursorAdapter
    {
        private LayoutInflater mInflater;
        private Cursor cur;
        private Context context;
    	Location origem;

        public ItemListaCursorAdapter(Context context, Cursor c) {
            super(context,c);       
            this.mInflater = LayoutInflater.from(context);
            this.cur = c;
            this.context = context;
    		origem = MapUtils.getMyLocation( context );
        }
        public ItemListaCursorAdapter(Context context, Cursor c, boolean autoRequery)
        {
            super(context, c, autoRequery);
            this.mInflater = LayoutInflater.from(context);
            this.cur = c;
            this.context = context;
    		origem = MapUtils.getMyLocation( context );
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder;
            if(convertView == null)
            {
                convertView = this.mInflater.inflate(R.layout.lista_casas_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tipoImage = (ImageView)convertView.findViewById(R.id.tipoImage);
                viewHolder.nome = (TextView)convertView.findViewById(R.id.nome);
                viewHolder.endereco = (TextView)convertView.findViewById(R.id.endereco);
                viewHolder.bairro = (TextView)convertView.findViewById(R.id.bairro);
                viewHolder.distancia = (TextView)convertView.findViewById(R.id.distancia);
                convertView.setTag(viewHolder);
            }else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            this.cur.moveToPosition(position);

            viewHolder.nome.setText(this.cur.getString(this.cur.getColumnIndex(Casas.NOME)));          
            viewHolder.endereco.setText(this.cur.getString(this.cur.getColumnIndex(Casas.ENDERECO)));
            viewHolder.bairro.setText(this.cur.getString(this.cur.getColumnIndex(Casas.BAIRRO)));
            Double lat = this.cur.getDouble(this.cur.getColumnIndex(Casas.LAT));
			Double lng = this.cur.getDouble(this.cur.getColumnIndex(Casas.LNG));
            String distancia = getCalcularDistancia(lat, lng);
            viewHolder.distancia.setText(distancia); 
            getTipoImagem(this.cur.getString(this.cur.getColumnIndex(Casas.TYPE)), viewHolder.tipoImage);

            return convertView;
        }
        /* (non-Javadoc)
         * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Dont need to do anything here

        }
        /* (non-Javadoc)
         * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Dont need to do anything here either
            return null;
        }
        
        private String getCalcularDistancia(Double lat, Double lng){

			Location destino = new Location("Centro");
			destino.setLatitude(lat);
			destino.setLongitude(lng);
			
			String novoTexto = MapUtils.getDistanceFormatada(origem, destino);
			return novoTexto;
        }
        
        private void getTipoImagem(String tipo, ImageView imagem){
						
			if(tipo != null && tipo.equals("centro")){
				imagem.setImageDrawable(this.context.getResources().getDrawable(R.drawable.list_icon_entidade));
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
        }

        static class ViewHolder {
            TextView nome;
            TextView endereco;
            TextView bairro;
            TextView distancia;
            ImageView tipoImage;
            ProgressBar progresso;
        }
    }