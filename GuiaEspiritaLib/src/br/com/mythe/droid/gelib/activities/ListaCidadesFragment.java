package br.com.mythe.droid.gelib.activities;

import java.util.ArrayList;
import java.util.List;

import org.jredfoot.sophielib.util.AdMobsUtil;
import org.jredfoot.sophielib.util.NetUtils;
import org.jredfoot.sophielib.util.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseListFragment;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaCidadesFragment extends BaseListFragment {

	String estado = null;
	String pais = null;
	
	BaseListFragment _self = this;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_lista_cidades);
	}
	
	@Override
	protected void inicializar(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			estado = savedInstanceState.getString("estado");
			pais = savedInstanceState.getString("pais");
		} else {
			Bundle intent = getArguments();
			estado = intent.getString("estado");
			pais = intent.getString("pais");
		}
	}
	
	@Override
	protected void popularTela() {
		String[] projection = new String[] { Casas._ID, Casas.CIDADE,
				Casas.ESTADO, " count(*) AS " + Casas.QTDE };

		String[] displayFields = new String[] { Casas.CIDADE, Casas.ESTADO,
				Casas.QTDE };

		int[] displayViews = new int[] { R.id.cidade, R.id.estado,
				R.id.quantidade, };

		if (estado != null && estado.trim().length() <= 2) {
			getActivity().setTitle(Utils.getEstado(estado));
		} else {
			getActivity().setTitle(estado);
		}

		setTitleFromActivityLabel(R.id.title_text);

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

		selArgs = new String[selArgsL.size()];

		selArgs = selArgsL.toArray(selArgs);

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
					if (estado != null)
						intent.putString("estado", estado);
					if(pais != null)
						intent.putString("pais", pais);
					startFragment(fragment, intent, HomeActivity.FRAGMENT_NOVO_ITEM);

				} else {
					abrirDialogLogin(getActivity(), true);
				}

			}
		});

		getListView().addFooterView(view);

		Cursor cur = getActivity().getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT_CIDADES ), projection,
				selection, selArgs, null);
		setListAdapter(new SimpleCursorAdapter(getActivity(),
				R.layout.lista_cidades_item, cur, displayFields, displayViews));
					
		if(isLite()){
			AdMobsUtil.addAdView(getActivity());
		}	
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		String cidade = c.getString(1);
		
		ListaItensFragment bdf = new ListaItensFragment();
		Bundle bundle = new Bundle();
		bundle.putString("pais", pais);
		bundle.putString("estado", estado);
		bundle.putString("cidade", cidade);
		startFragment(bdf, bundle, HomeActivity.FRAGMENT_LISTA_ITEMS);
		
	}

}
