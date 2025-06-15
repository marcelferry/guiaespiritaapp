package br.com.mythe.droid.gelib.activities;

import java.util.ArrayList;
import java.util.List;

import org.jredfoot.sophielib.util.AdMobsUtil;
import org.jredfoot.sophielib.util.NetUtils;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseListFragment;
import br.com.mythe.droid.gelib.cursor.ItemListaCursorAdapter;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.dialog.ListClickDialog;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaItensFragment extends BaseListFragment implements
		OnItemLongClickListener {

	private AlertDialog mListClickDialog;
	private String pais = null;
	private String cidade = null;
	private String estado = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_lista_itens);
	}
	
	@Override
	protected void inicializar(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			pais = savedInstanceState.getString("pais");
			estado = savedInstanceState.getString("estado");
			cidade = savedInstanceState.getString("cidade");
		} else {
			Bundle intent = getArguments();
			pais = intent.getString("pais");			
			cidade = intent.getString("cidade");
			estado = intent.getString("estado");
		}
	}
	
	@Override
	protected void popularTela() {

		setTitle("Entidades de " + cidade);

		setTitleFromActivityLabel(R.id.title_text);

		String[] projection = new String[] { Casas._ID, Casas.COD_CASA,
				Casas.NOME, Casas.ENDERECO, Casas.BAIRRO, Casas.CIDADE,
				Casas.SITE, Casas.LAT, Casas.LNG, Casas.TYPE };

		RelativeLayout view = (RelativeLayout) getActivity().getLayoutInflater()
				.inflate(R.layout.footer_add_button, null);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				
				if (!NetUtils.isNetworkConnected(getActivity())) {				
					toast("Não há conexão no momento. Tente mais tarde.");
					return;
				}
				
				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				boolean logado = settings.getBoolean("logado", false);
				if (logado) {
					Fragment fragment = new NovoItemFragment();
					Bundle intent = new Bundle();
					if (cidade != null)
						intent.putString("cidade", cidade);
					if (estado != null)
						intent.putString("estado", estado);
					if(pais != null)
						intent.putString("pais", pais);
					startFragment(fragment, intent, null);
				} else {
					abrirDialogLogin(getActivity(), true);
				}
			}
		});

		getListView().addFooterView(view);

		
		
		String selection = "";
		String selArgs[] = null;
		List<String> selArgsL = new ArrayList<String>();

		if (estado != null) {
			selection = "lower(" + Casas.ESTADO + ") like lower(?)";
			selArgsL.add(estado);
		}

		if (pais == null) {
			pais = "Brasil";
		}

		if (!selection.equals("")) {
			selection += " and ";
		}

		selection += "lower(" + Casas.PAIS + ") like lower(?)";
		selArgsL.add(pais);
		
		if (!selection.equals("")) {
			selection += " and ";
		}
		
		selection += "lower(" + Casas.CIDADE + ") like lower( ? )";
		
		selArgsL.add(cidade);

		selArgs = new String[selArgsL.size()];

		selArgs = selArgsL.toArray(selArgs);
		
		
		Cursor cur = getActivity().getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), projection, selection,
				selArgs, Casas.NOME + " ASC");
		//setListAdapter(new CasasListaCursor(getActivity(), R.layout.lista_casas_item,
		//		cur, displayFields, displayViews));
		
		setListAdapter(new ItemListaCursorAdapter(getActivity(), cur, true));

		getListView().setOnItemLongClickListener(this);
		
		if(isLite()){
			AdMobsUtil.addAdView(getActivity());
		}
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("pais", pais);
		outState.putString("estado", estado);
		outState.putString("cidade", cidade);		

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		Integer idItem = c.getInt(1);

		Intent i = new Intent(getActivity(), ItemDetalheActivity.class);
		i.putExtra("id_centro", idItem);
		startActivity(i);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> l, View view, int position,
			long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		Integer idItem = c.getInt(1);
		AlertDialog.Builder builder = null;
		String site = c.getString(c.getColumnIndex(Casas.SITE));
		if (site != null && site.trim().length() > 7) {
			builder = new ListClickDialog.Builder(new ContextThemeWrapper(getActivity(),
					R.style.Theme_D1dialog), idItem, site);
		} else {
			builder = new ListClickDialog.Builder(new ContextThemeWrapper(getActivity(),
					R.style.Theme_D1dialog), idItem);
		}

		mListClickDialog = builder.create();
		mListClickDialog.show();

		return true;
	}

}
