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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.ListaActivity;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaCidadesActivity extends ListaActivity {

	String estado = null;
	String pais = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_lista_cidades);

		String[] projection = new String[] { Casas._ID, Casas.CIDADE,
				Casas.ESTADO, " count(*) AS " + Casas.QTDE };

		String[] displayFields = new String[] { Casas.CIDADE, Casas.ESTADO,
				Casas.QTDE };

		int[] displayViews = new int[] { R.id.cidade, R.id.estado,
				R.id.quantidade, };

		if (savedInstanceState != null) {
			estado = savedInstanceState.getString("estado");
			pais = savedInstanceState.getString("pais");
		} else {
			Intent intent = getIntent();
			estado = intent.getStringExtra("estado");
			pais = intent.getStringExtra("pais");
		}

		if (estado != null && estado.trim().length() <= 2) {
			setTitle(Utils.getEstado(estado));
		} else {
			setTitle(estado);
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

		RelativeLayout view = (RelativeLayout) this.getLayoutInflater()
				.inflate(R.layout.footer_add_button, null);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				
				if (!NetUtils.isNetworkConnected(ListaCidadesActivity.this)) {				
					toast("Não há conexão no momento. Tente mais tarde.");
					return;
				}
				
				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				boolean logado = settings.getBoolean("logado", false);
				if (logado) {
					Intent intent = new Intent(ListaCidadesActivity.this,
							NovoItemActivity.class);
					if (estado != null)
						intent.putExtra("estado", estado);
					if(pais != null)
						intent.putExtra("pais", pais);
					startActivity(intent);

				} else {
					abrirDialogLogin(ListaCidadesActivity.this, true);
				}

			}
		});

		getListView().addFooterView(view);

		Cursor cur = managedQuery(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT_CIDADES ), projection,
				selection, selArgs, null);
		setListAdapter(new SimpleCursorAdapter(this,
				R.layout.lista_cidades_item, cur, displayFields, displayViews));
					
		if(isLite()){
			AdMobsUtil.addAdView(this);
		}
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		String cidade = c.getString(1);
		Intent intent = new Intent(this, ListaItensActivity.class);
		intent.putExtra("pais", pais);
		intent.putExtra("estado", estado);
		intent.putExtra("cidade", cidade);
		startActivity(intent);
	}

}
