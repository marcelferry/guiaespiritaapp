package br.com.mythe.droid.gelib.activities;

import org.jredfoot.sophielib.util.AdMobsUtil;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseListFragment;
import br.com.mythe.droid.gelib.cursor.ItemListaCursorAdapter;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.dialog.ListClickDialog;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaFavoritosFragment extends BaseListFragment {

	private AlertDialog mListClickDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_lista_favoritos);
	}
	
	@Override
	protected void inicializar(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void popularTela() {
	
		setTitle(getActivity().getString( R.string.title_itens_favoritos ));
		
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
		
		Cursor cur = getActivity().getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT_FAVORITAS ), projection, null, null, null);		
		setListAdapter(new ItemListaCursorAdapter(getActivity(), cur, true));

		if(isLite()){
			AdMobsUtil.addAdView(getActivity());
		}
		
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		Integer idItem = c.getInt(1);
		AlertDialog.Builder builder = new ListClickDialog.Builder(
				new ContextThemeWrapper(getActivity(), R.style.Theme_D1dialog), idItem);
		mListClickDialog = builder.create();
		mListClickDialog.show();
    } 
}
