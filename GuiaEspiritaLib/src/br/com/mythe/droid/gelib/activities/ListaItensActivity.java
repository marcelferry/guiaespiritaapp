package br.com.mythe.droid.gelib.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import br.com.mythe.droid.common.util.AdMobsUtil;
import br.com.mythe.droid.common.util.NetUtils;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.ListaActivity;
import br.com.mythe.droid.gelib.cursor.ItemListaCursorAdapter;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.dialog.ListClickDialog;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class ListaItensActivity extends ListaActivity implements
		OnItemLongClickListener {

	private AlertDialog mListClickDialog;
	private String pais = null;
	private String cidade = null;
	private String estado = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_lista_itens);

		if (savedInstanceState != null) {
			pais = savedInstanceState.getString("pais");
			estado = savedInstanceState.getString("estado");
			cidade = savedInstanceState.getString("cidade");
		} else {
			Intent intent = getIntent();
			pais = intent.getStringExtra("pais");			
			cidade = intent.getStringExtra("cidade");
			estado = intent.getStringExtra("estado");
		}

		setTitle("Entidades de " + cidade);

		setTitleFromActivityLabel(R.id.title_text);

		String[] projection = new String[] { Casas._ID, Casas.COD_CASA,
				Casas.NOME, Casas.ENDERECO, Casas.BAIRRO, Casas.CIDADE,
				Casas.SITE, Casas.LAT, Casas.LNG, Casas.TYPE };

		String[] displayFields = new String[] {
				// Casas.COD_CASA,
				Casas.NOME, Casas.ENDERECO, Casas.BAIRRO };

		int[] displayViews = new int[] {
				// R.id.codigo,
				R.id.nome, R.id.endereco, R.id.bairro };

		RelativeLayout view = (RelativeLayout) this.getLayoutInflater()
				.inflate(R.layout.footer_add_button, null);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				
				if (!NetUtils.isNetworkConnected(ListaItensActivity.this)) {				
					toast("Não há conexão no momento. Tente mais tarde.");
					return;
				}
				
				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				boolean logado = settings.getBoolean("logado", false);
				if (logado) {
					Intent intent = new Intent(ListaItensActivity.this,
							NovoItemActivity.class);
					if (pais != null)
						intent.putExtra("pais", pais);
					if (estado != null)
						intent.putExtra("estado", estado);
					if (cidade != null)
						intent.putExtra("cidade", cidade);
					startActivity(intent);
				} else {
					abrirDialogLogin(ListaItensActivity.this, true);
				}
			}
		});

		getListView().addFooterView(view);

		String selection = Casas.CIDADE + " like ?";
		Cursor cur = managedQuery(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), projection, selection,
				new String[] { cidade }, Casas.NOME + " ASC");
		//setListAdapter(new CasasListaCursor(this, R.layout.lista_casas_item,
		//		cur, displayFields, displayViews));
		
		setListAdapter(new ItemListaCursorAdapter(this, cur, true));

		getListView().setOnItemLongClickListener(this);
		
		if(isLite()){
			AdMobsUtil.addAdView(this);
		}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("pais", pais);
		outState.putString("estado", estado);
		outState.putString("cidade", cidade);		

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		Integer idItem = c.getInt(1);

		Intent i = new Intent(this, ItemDetalheActivity.class);
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
			builder = new ListClickDialog.Builder(new ContextThemeWrapper(this,
					R.style.Theme_D1dialog), idItem, site);
		} else {
			builder = new ListClickDialog.Builder(new ContextThemeWrapper(this,
					R.style.Theme_D1dialog), idItem);
		}

		mListClickDialog = builder.create();
		mListClickDialog.show();

		return true;
	}

}
