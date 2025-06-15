package br.com.mythe.droid.gelib.activities;

import org.jredfoot.sophielib.util.AdMobsUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseListFragment;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaPaisesFragment extends BaseListFragment {

	private SharedPreferences mSettings;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_lista_paises);
	}
	
	@Override
	protected void inicializar(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void popularTela() {
	
		mSettings = PreferenceManager
				.getDefaultSharedPreferences(getActivity());


		String[] projection = new String[] { Casas._ID, Casas.PAIS,
				" count(*) AS " + Casas.QTDE };

		String[] displayFields = new String[] { Casas.PAIS, Casas.QTDE };

		int[] displayViews = new int[] { R.id.pais, R.id.quantidade, };

		String selection = null;
		String selArgs[] = null;

		String savedCountry = mSettings.getString("pais", "Brasil");

		selection = "lower(" + Casas.PAIS + ") <> lower(?)";
		selArgs = new String[] { savedCountry };

		Cursor cur = getActivity().getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT_PAISES ), projection,
				selection, selArgs, null);
		setListAdapter(new SimpleCursorAdapter(getActivity(),
				R.layout.lista_paises_item, cur, displayFields, displayViews));

		if(isLite()){
			AdMobsUtil.addAdView(getActivity());
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.listas, menu);
		super.onPrepareOptionsMenu(menu);
	}

	/** Handles menu clicks */
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.add_item) {
			Fragment fragment = new NovoItemFragment();
			Bundle intent = new Bundle();
			startFragment(fragment, intent, null);
			return true;
		}

		return false;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		String pais = c.getString(1);
		
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ListaEstadosFragment bdf = new ListaEstadosFragment();
		Bundle bundle = new Bundle();
		bundle.putString("pais", pais);
		bdf.setArguments(bundle);
		ft.replace(R.id.content_frame, bdf);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
		
	}
	

}
