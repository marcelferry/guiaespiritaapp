package br.com.mythe.droid.gelib.activities;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ListView;
import br.com.mythe.droid.common.util.AdMobsUtil;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.ListaActivity;
import br.com.mythe.droid.gelib.cursor.ItemListaCursorAdapter;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.dialog.ListClickDialog;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaFavoritosActivity extends ListaActivity {

	private AlertDialog mListClickDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_lista_favoritos);
		
		setTitle(R.string.title_itens_favoritos);
		
		setTitleFromActivityLabel(R.id.title_text);
			
		String[] projection = new String[]{	
				Casas._ID,
				Casas.COD_CASA,
				Casas.NOME,
				Casas.ENDERECO,
				Casas.BAIRRO,
				Casas.CIDADE,
				Casas.LAT,
				Casas.LNG,
				Casas.TYPE
			};
		
		String[] displayFields = new String[]{
				//Casas.COD_CASA,
				Casas.NOME,
				Casas.ENDERECO,
				Casas.BAIRRO
			};
		
		
		int[] displayViews = new int[]{
				//R.id.codigo,
				R.id.nome,
				R.id.endereco,
				R.id.bairro
			};
		
		//String selection = Favoritos.STAR + " like ?";
		Cursor cur = managedQuery(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT_FAVORITAS ), projection, null, null, null);		
		//setListAdapter(new CasasListaCursor(this, R.layout.lista_casas_item, cur, displayFields, displayViews));
		setListAdapter(new ItemListaCursorAdapter(this, cur, true));

		if(isLite()){
			AdMobsUtil.addAdView(this);
		}
		
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		Integer idItem = c.getInt(1);
		AlertDialog.Builder builder = new ListClickDialog.Builder(
				new ContextThemeWrapper(this, R.style.Theme_D1dialog), idItem);
		mListClickDialog = builder.create();
		mListClickDialog.show();
    } 
}
