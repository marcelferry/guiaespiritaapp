package br.com.mythe.droid.gelib.activities;

import org.jredfoot.sophielib.util.AdMobsUtil;
import org.jredfoot.sophielib.util.NetUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseListFragment;
import br.com.mythe.droid.gelib.cursor.EstadosListaCursor;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaEstadosFragment extends BaseListFragment {

	private String pais = null;
	private SharedPreferences mSettings;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_lista_estados);
	}
	
	@Override
	protected void inicializar(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			pais = savedInstanceState.getString("pais");
		} else {
			Bundle intent = getArguments();
			pais = intent.getString("pais");
		}
	}
	
	@Override
	protected void popularTela() {
		mSettings = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		String[] projection = new String[] { Casas._ID, Casas.ESTADO,
				" count(*) AS " + Casas.QTDE };

		String[] displayFields = new String[] { Casas.ESTADO, Casas.QTDE };

		int[] displayViews = new int[] { R.id.estado, R.id.quantidade, };

		setTitle("Lista Estados");

		setTitleFromActivityLabel(R.id.title_text);

		String selection = null;
		String selArgs[] = null;

		if (pais != null) {
			selection = "lower(" + Casas.PAIS + ") like lower(?)";
			selArgs = new String[] { pais };
		} else {
			pais = mSettings.getString("pais", "Brasil");
			selection = "lower(" + Casas.PAIS + ") like lower(?)";
			selArgs = new String[] { pais };
		}

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
					if(pais != null)
						intent.putString("pais", pais);
					startFragment(fragment, intent, HomeActivity.FRAGMENT_NOVO_ITEM);
				} else {
					abrirDialogLogin(getActivity(), true);
				}
			}
		});

		getListView().addFooterView(view);

		Cursor cur = getActivity().getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT_ESTADOS ), projection,
				selection, selArgs, null);
		setListAdapter(new EstadosListaCursor(getActivity(),
				R.layout.lista_estados_item, cur, displayFields, displayViews));

		if(isLite()){
			AdMobsUtil.addAdView(getActivity());
		}

	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		String estado = c.getString(1);
		
		ListaCidadesFragment bdf = new ListaCidadesFragment();
		Bundle bundle = new Bundle();
		bundle.putString("estado", estado);
		bundle.putString("pais", pais);
		startFragment(bdf, bundle, HomeActivity.FRAGMENT_LISTA_CIDADES);

	}

}
