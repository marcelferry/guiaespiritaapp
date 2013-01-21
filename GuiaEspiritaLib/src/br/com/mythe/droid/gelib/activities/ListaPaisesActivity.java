package br.com.mythe.droid.gelib.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import br.com.mythe.droid.common.util.AdMobsUtil;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.ListaActivity;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaPaisesActivity extends ListaActivity {

	private SharedPreferences mSettings;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSettings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		setContentView(R.layout.activity_lista_paises);

		String[] projection = new String[] { Casas._ID, Casas.PAIS,
				" count(*) AS " + Casas.QTDE };

		String[] displayFields = new String[] { Casas.PAIS, Casas.QTDE };

		int[] displayViews = new int[] { R.id.pais, R.id.quantidade, };

		String selection = null;
		String selArgs[] = null;

		String savedCountry = mSettings.getString("pais", "Brasil");

		selection = "lower(" + Casas.PAIS + ") <> lower(?)";
		selArgs = new String[] { savedCountry };

		Cursor cur = managedQuery(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT_PAISES ), projection,
				selection, selArgs, null);
		setListAdapter(new SimpleCursorAdapter(this,
				R.layout.lista_paises_item, cur, displayFields, displayViews));

		if(isLite()){
			AdMobsUtil.addAdView(this);
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listas, menu);
		return super.onPrepareOptionsMenu(menu);
	}

	/** Handles menu clicks */
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.add_item) {
			Intent intent = new Intent(this, NovoItemActivity.class);
			startActivity(intent);
			return true;
		}

		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		String pais = c.getString(1);
		Intent intent = new Intent(this, ListaEstadosActivity.class);
		intent.putExtra("pais", pais);
		startActivity(intent);

	}
	

}
