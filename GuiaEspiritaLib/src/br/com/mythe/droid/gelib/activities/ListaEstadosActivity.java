package br.com.mythe.droid.gelib.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import br.com.mythe.droid.common.util.AdMobsUtil;
import br.com.mythe.droid.common.util.NetUtils;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.ListaActivity;
import br.com.mythe.droid.gelib.cursor.EstadosListaCursor;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaEstadosActivity extends ListaActivity {

	private String pais = null;
	private SharedPreferences mSettings;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_lista_estados);

		mSettings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		String[] projection = new String[] { Casas._ID, Casas.ESTADO,
				" count(*) AS " + Casas.QTDE };

		String[] displayFields = new String[] { Casas.ESTADO, Casas.QTDE };

		int[] displayViews = new int[] { R.id.estado, R.id.quantidade, };

		if (savedInstanceState != null) {
			pais = savedInstanceState.getString("pais");
		} else {
			Intent intent = getIntent();
			pais = intent.getStringExtra("pais");
		}

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

		RelativeLayout view = (RelativeLayout) this.getLayoutInflater()
				.inflate(R.layout.footer_add_button, null);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {

				if (!NetUtils.isNetworkConnected(ListaEstadosActivity.this)) {
					toast("Não há conexão no momento. Tente mais tarde.");
					return;
				}

				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				boolean logado = settings.getBoolean("logado", false);
				if (logado) {
					Intent intent = new Intent(ListaEstadosActivity.this,
							NovoItemActivity.class);
					intent.putExtra("pais", pais);
					startActivity(intent);
				} else {
					abrirDialogLogin(ListaEstadosActivity.this, true);
				}
			}
		});

		getListView().addFooterView(view);

		Cursor cur = managedQuery(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT_ESTADOS ), projection,
				selection, selArgs, null);
		setListAdapter(new EstadosListaCursor(this,
				R.layout.lista_estados_item, cur, displayFields, displayViews));

		// getListView().setBackgroundResource(R.drawable.rounded_corner_full);

		if(isLite()){
			AdMobsUtil.addAdView(this);
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		String estado = c.getString(1);
		Intent intent = new Intent(this, ListaCidadesActivity.class);
		intent.putExtra("estado", estado);
		intent.putExtra("pais", pais);
		startActivity(intent);

	}

}
