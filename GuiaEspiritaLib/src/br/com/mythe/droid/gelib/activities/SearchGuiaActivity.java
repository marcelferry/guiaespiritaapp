package br.com.mythe.droid.gelib.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ListView;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseListFragment;
import br.com.mythe.droid.gelib.cursor.CasasListaCursor;
import br.com.mythe.droid.gelib.cursor.ItemListaCursorAdapter;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.dialog.ListClickDialog;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;
import br.com.mythe.droid.gelib.provider.SuggestionProviderFull;

public class SearchGuiaActivity extends ListActivity {

	private static final int SEARCHING_DIALOG = 0;
	private Dialog mInfoDialog;
	private AlertDialog mListClickDialog;

	private ProgressDialog mSearchingDialog;
	private String mFilterString;

	@Override
	public void onCreate(Bundle in) {
		super.onCreate(in);

		setContentView(R.layout.activity_lista_itens);

		//if (in == null) {
			Intent intent = getIntent();
			handleSearch(intent);
			
		//}

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleSearch(intent);
	}
	
	private void handleSearch(Intent intent){
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
					this, SuggestionProviderFull.AUTHORITY,
					SuggestionProviderFull.MODE);
			suggestions.saveRecentQuery(query, null);
		}
		mFilterString = intent.getStringExtra(SearchManager.QUERY);

		setTitle("Resultado da Pesquisa: " + mFilterString);

		executeSearch();
	}

	private void executeSearch() {
		String[] projection = new String[] { Casas._ID, Casas.COD_CASA,
				Casas.NOME, Casas.ENDERECO, Casas.BAIRRO, Casas.CIDADE,
				Casas.LAT, Casas.LNG,
				Casas.TYPE };

		mFilterString = mFilterString.replaceAll(" ", "%");
		String selection = " lower(" + Casas.NOME + ") like lower(?) " +
				       "  OR lower(" + Casas.ENDERECO + ") like lower(?)" +
				       "  OR lower(" + Casas.BAIRRO + ") like lower(?)" +
				       "  OR lower(" + Casas.CEP + ") like lower(?)" +
				       "  OR lower(" + Casas.CIDADE + ") like lower(?)";
		Cursor cur = getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), projection, selection,
				new String[] { "%" + mFilterString + "%",
						"%" + mFilterString + "%",
						"%" + mFilterString + "%",
						"%" + mFilterString + "%",
						"%" + mFilterString + "%" }, null);

		//setListAdapter(new CasasListaCursor(this, R.layout.lista_casas_item,
		//		cur, displayFields, displayViews));
		
		setListAdapter(new ItemListaCursorAdapter(this, cur, true));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		Integer idItem = c.getInt(1);
		AlertDialog.Builder builder = new ListClickDialog.Builder(
				new ContextThemeWrapper(this, R.style.Theme_D1dialog), idItem);
		mListClickDialog = builder.create();
		mListClickDialog.show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case SEARCHING_DIALOG: 
				if (mSearchingDialog == null) {
					mSearchingDialog = new ProgressDialog(this,
							R.style.Theme_D1dialog);
					mSearchingDialog.setMessage("Localizando, aguarde.");
					mSearchingDialog.setIndeterminate(true);
					mSearchingDialog.setCancelable(false);
				}
				return mSearchingDialog;
			
		}
		return null;
	}
	
	protected boolean isLite() {
		return getPackageName().toLowerCase().contains("lite");
	}

}
