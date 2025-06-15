package br.com.mythe.droid.gelib.activities;

import java.util.HashMap;
import java.util.Map;

import org.jredfoot.sophielib.exception.NegocioException;
import org.jredfoot.sophielib.util.MapUtils;
import org.jredfoot.sophielib.util.NetUtils;
import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.DashboardActivity;
import br.com.mythe.droid.gelib.constants.GEConst;
import br.com.mythe.droid.gelib.database.GuiaEspiritaDB;
import br.com.mythe.droid.gelib.util.GelibUtils;

/**
 * This is a simple activity that demonstrates the dashboard user interface pattern.
 *
 */

public class HomeActivity extends DashboardActivity 
{

	public final static String FRAGMENT_DASHBOARD = "DASHBOARD";
	public final static String FRAGMENT_LISTA_ESTADOS = "LISTA_ESTADOS";
	public final static String FRAGMENT_LISTA_CIDADES = "LISTA_CIDADES";
	public final static String FRAGMENT_LISTA_PAISES = "LISTA_PAISES";
	public final static String FRAGMENT_LISTA_ITEMS = "LISTA_ITEMS";
	public final static String FRAGMENT_LISTA_PROXIMOS = "LISTA_PROXIMOS";
	public final static String FRAGMENT_LISTA_FAVORITOS = "LISTA_FAVORITOS";
	public final static String FRAGMENT_RADAR = "RADAR";
	public final static String FRAGMENT_NOVO_ITEM = "NOVO_ITEM";
	public final static String FRAGMENT_SOBRE = "SOBRE";
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuItemTitles;
	
	private static final int ABOUT_DIALOG = 0;
	private static final int ALERT_DIALOG = 2;

	private Dialog mAboutDialog;
	private AlertDialog mAlertDialog;
	
	private boolean showUpdates = true;

	Context _self = this;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GelibUtils.NETWOTK_ERROR_WAIT:
				Toast.makeText(
						HomeActivity.this,
						"Erro ao sincronizar conteúdo Web, verifique sua conexão ou tente novamente mais tarde.",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	public static Handler mStaticHandler = null;
	
	
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		if(savedInstanceState != null){
			showUpdates = savedInstanceState.getBoolean("showUpdates", true);
		}
	    
	    mTitle = mDrawerTitle = getTitle();
	    mMenuItemTitles = getResources().getStringArray(R.array.planets_array);
	    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    mDrawerList = (ListView) findViewById(R.id.left_drawer);
	    
	    // set a custom shadow that overlays the main content when the drawer opens
	    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	    // set up the drawer's list view with items and click listener
	    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
	            R.layout.drawer_list_item, mMenuItemTitles));
	    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	    
	    // enable ActionBar app icon to behave as action to toggle nav drawer
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setHomeButtonEnabled(true);
	
	    // ActionBarDrawerToggle ties together the the proper interactions
	    // between the sliding drawer and the action bar app icon
	    mDrawerToggle = new ActionBarDrawerToggle(
	            this,                  /* host Activity */
	            mDrawerLayout,         /* DrawerLayout object */
	            R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
	            R.string.drawer_open,  /* "open drawer" description for accessibility */
	            R.string.drawer_close  /* "close drawer" description for accessibility */
	            ) {
	        public void onDrawerClosed(View view) {
	            getSupportActionBar().setTitle(mTitle);
	            supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	        }
	
	        public void onDrawerOpened(View drawerView) {
	            getSupportActionBar().setTitle(mDrawerTitle);
	            supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	        }
	    };
	    mDrawerLayout.setDrawerListener(mDrawerToggle);
	
	    if (savedInstanceState == null) {
	        selectItem(0);
	    }
		
	
	    if(showUpdates){
			int qtde = GuiaEspiritaDB.getQtdeCasas(this);
			if(qtde == 0){
				showDialog(ALERT_DIALOG);
			} else {
				if(NetUtils.isNetworkConnected(this)){
					try{
						String update[] = GuiaEspiritaDB.getLastTimestamp(this);
						new Sincronizar().execute(update);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("GuiaEspirita", e.getMessage());
					}
				}
			}
	    }
	
		if(isLite()){
			//AdMobsUtil.addAdView(this);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    
	    // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    MenuItem searchItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    // If the nav drawer is open, hide action items related to the content view
	    boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	    //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
	    return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	     // The action bar home/up action should open or close the drawer.
	     // ActionBarDrawerToggle will take care of this.
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
	        return true; 
	    }
	    // Handle action buttons
	    if(item.getItemId() ==  R.id.action_settings){
    		startActivity(new Intent(this, Preferences.class));
    		return true;
	    }else if(item.getItemId() ==  R.id.action_newitem){
	    	Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_NOVO_ITEM);
	    	if (fragment != null && fragment.isVisible()) {
	    		// FIXME:
	    	} else {
				fragment = new NovoItemFragment();
				Bundle args = new Bundle();
			    startFragment(fragment, args, FRAGMENT_NOVO_ITEM);
	    	}
    		return true;
	    }else {
            return super.onOptionsItemSelected(item);
        }
	}
	
	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}
	
	private void selectItem(int position) {
	    // update the main content by replacing fragments
		Fragment fragment = null;
		String fragmentTag = "";
		boolean frag = true;
		boolean stack = true;
		
		
		Fragment teste = getSupportFragmentManager().findFragmentByTag(HomeActivity.FRAGMENT_DASHBOARD);
		if(teste != null && teste.isVisible()){
			stack = true;
		}
		
		switch (position) {
			case 0:
				fragmentTag = HomeActivity.FRAGMENT_DASHBOARD;
				fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
				if (fragment != null && fragment.isVisible()) {
				   frag = false;
				} else {
					fragment = new DashboardFragment();
				}
				break;
			case 1:
				fragmentTag = HomeActivity.FRAGMENT_LISTA_ESTADOS;
				fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
				if (fragment != null && fragment.isVisible()) {
				   frag = false;
				} else {
					fragment = new ListaEstadosFragment();
				}
				break;
			case 2:
				fragmentTag = HomeActivity.FRAGMENT_LISTA_FAVORITOS;
				fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
				if (fragment != null && fragment.isVisible()) {
				   frag = false;
				} else {
					fragment = new ListaFavoritosFragment();
				}
				break;
			case 3:
				if (!MapUtils.isGPSActive(this)) {
					GelibUtils.abrirDialogGPS(this);
					frag = false;
				} else {
					fragmentTag = HomeActivity.FRAGMENT_LISTA_PROXIMOS;
					fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
					if (fragment != null && fragment.isVisible()) {
					   frag = false;
					} else {
						fragment = new ListaProximosFragment();
					}
				}
				break;
			case 4:
				if (!MapUtils.isGPSActive(this)) {
					GelibUtils.abrirDialogGPS(this);
					frag = false;
				} else {
					if (NetUtils.isNetworkConnected(this)) {
						fragmentTag = HomeActivity.FRAGMENT_RADAR;
						fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
						if (fragment != null && fragment.isVisible()) {
						   frag = false;
						} else {
							//Correcao mapa
							Fragment fragmentMapa = getSupportFragmentManager().findFragmentById(R.id.map);
							if(fragmentMapa != null){
							    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
							    ft.remove(fragmentMapa);
							    ft.commit();
							}
							fragment = new RadarProximidadeFragment();
						}
					} else {
						toast("Não há conexão no momento. Tente mais tarde.");
						frag = false;
					}
				}
				break;
			case 5:
				fragmentTag = HomeActivity.FRAGMENT_LISTA_PAISES;
				fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
				if (fragment != null && fragment.isVisible()) {
				   frag = false;
				} else {
					fragment = new ListaPaisesFragment();
				}
				break;
			case 6:
				startActivity(new Intent(this, Preferences.class));
				frag = false;
				break;
			case 7:
				fragmentTag = HomeActivity.FRAGMENT_SOBRE;
				fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
				if (fragment != null && fragment.isVisible()) {
				   frag = false;
				} else {
					fragment = new AboutFragment();
				}
				break;
			default:
				break;
		}
	    
		if(frag){
			try{
				Bundle args = new Bundle();
			    startFragment(fragment, args, fragmentTag);
			}catch (RuntimeException e) {
				e.printStackTrace();
				Log.d("GELIB", "" + e.getLocalizedMessage());
			}
		}
	
	    // update selected item and title, then close the drawer
	    mDrawerList.setItemChecked(position, true);
	    setTitle(mMenuItemTitles[position]);
	    mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	@Override
	public void setTitle(CharSequence title) {
	    mTitle = title;
	    getSupportActionBar().setTitle(mTitle);
	}
	
	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    // Sync the toggle state after onRestoreInstanceState has occurred.
	    mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    // Pass any configuration change to the drawer toggls
	    mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	
	class Sincronizar extends AsyncTask<String, Integer, Integer>{
	
		@Override
		protected Integer doInBackground(String... params) {
			Document doc;
			Map<String, String> parametros = new HashMap<String, String>();
			parametros.put("last_id", params[0]);
			parametros.put("update", params[1]);
			String caminho;
			try {
				caminho = NetUtils.generateGetUrl(GEConst.LIST_ENTIDADES_URI, parametros);
				doc = NetUtils.getXmlFromService(caminho);	
				int casasSize = doc.getElementsByTagName("entidade").getLength();
				return casasSize;
			} catch (NegocioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		}
		
		@Override
		protected void onPostExecute(Integer casasSize) {
			if(casasSize > 0){
				abrirDialogSincronizar(casasSize);
			}
		}
	 
	 }
	   
	@Override
	protected void onStop() {
		if (mAboutDialog != null && mAboutDialog.isShowing())
			dismissDialog(ABOUT_DIALOG);
		if (mAlertDialog != null && mAlertDialog.isShowing())
			dismissDialog(ALERT_DIALOG);
	
		super.onStop();
	}
	
	
	/**
	 */
	// More Methods
	
	@Override
	public void onBackPressed() {
	    // TODO Auto-generated method stub

	    if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
	    	mDrawerLayout.closeDrawer(Gravity.LEFT);
	    }else{
			Fragment fragment = getSupportFragmentManager().findFragmentByTag(HomeActivity.FRAGMENT_DASHBOARD);
			if (fragment != null && fragment.isVisible()) {
				abrirDialogSair();
			} else {
				super.onBackPressed();
			}
	    }
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog d = null;
		if (id == ABOUT_DIALOG) {
			createAboutDialog();
			d = mAboutDialog;
		}
		if (id == ALERT_DIALOG) {
			createAlertDialog();
			d = mAlertDialog;
		}
		return d;
	}
	
	private void createAboutDialog() {
		AlertDialog.Builder builder;
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.about_dialog,
				(ViewGroup) findViewById(R.id.layout_about_root));
	
		((TextView) layout.findViewById(R.id.about_player_name_view))
				.setText(R.string.about_player_name);
		((TextView) layout.findViewById(R.id.about_bugs_link_view))
				.setText(R.string.about_bugs_link);
		((TextView) layout.findViewById(R.id.about_code_link_view))
				.setText(R.string.about_code_link);
		TextView text = (TextView) layout.findViewById(R.id.about_text_view);
	
		String version = "unknown";
		try {
			version = getPackageManager().getPackageInfo(
					"br.com.mythe.droid.ge", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String t = getString(R.string.about_text);
		t = String.format(t, version);
		text.setText(t);
	
		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		mAboutDialog = builder.create();
		mAboutDialog.setCancelable(true);
		mAboutDialog.setCanceledOnTouchOutside(true);
	}

	private void createAlertDialog() {
	
		mAlertDialog = new AlertDialog.Builder(
	        this).create();
	
		// Setting Dialog Title
		mAlertDialog.setTitle("Bem Vindo ao Guia Espírita");
		
		// Setting Dialog Message
		mAlertDialog.setMessage("Antes de começarmos, é preciso estar conectado a internet e fazer uma atualização da base de dados.");
		
		// Setting Icon to Dialog
		mAlertDialog.setIcon(android.R.drawable.checkbox_on_background);
		
		// Setting OK Button
		mAlertDialog.setButton(0, "OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	new SyncProgressTask(getApplicationContext(), null);
		    }
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("showUpdates", showUpdates);
		super.onSaveInstanceState(outState);
	}


	public void abrirDialogSincronizar(int casasSize){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
	
		alert.setTitle("Sincronizar Base");
		alert.setMessage("Existe(m) " + casasSize + " entidade(s) nova(s) ou atualizada(s). Atualizar a base de dados?");
	
		alert.setPositiveButton("Sincronizar", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			new SyncProgressTask(_self, mStaticHandler).execute(false);
	        showUpdates = false;
		  }
		});
	
		alert.setNegativeButton("Agora não", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  // Canceled.
			  showUpdates = false;
		  }
		});
	
		alert.show();
		// see http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
	}
	
	public void abrirDialogSair(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Sair do Aplicativo");
		alert.setMessage("Deseja sair e fechar o aplicativo?");
		
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		});
		
		alert.setNegativeButton("Agora não", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});
		
		alert.show();
		// see http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
	}
	
	public void startFragment(Fragment fragment, Bundle args, String tag, boolean back){
	    fragment.setArguments(args);
	
	    FragmentManager fragmentManager = getSupportFragmentManager();
	    FragmentTransaction ft = fragmentManager.beginTransaction();
	    if(tag != null){
	    	ft.replace(R.id.content_frame, fragment, tag);
	    } else {
	    	ft.replace(R.id.content_frame, fragment);
	    }
	    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    if(back){
	    	ft.addToBackStack(tag);
	    }
		ft.commit();
	}
	
	public void startFragment(Fragment fragment, Bundle args, String tag){
		startFragment(fragment, args, tag, true);
		//fragment.setArguments(args);
		//showFragment( R.id.content_frame, fragment, tag, getLastMenuPushed(), true );
        
	}
	
	public void showMenuFragment( Fragment fragment, String tag, boolean addToBackStack ) {
        showFragment( R.id.content_frame, fragment, tag, getLastMenuPushed(), addToBackStack );
        setLastMenuPushed( tag );
	}

	private String getLastMenuPushed() {
		return lastMenuPushed;
	}

	private void setLastMenuPushed(String tag) {
		this.lastMenuPushed = tag;
	}
	
	String lastMenuPushed;

	protected void showFragment( int resId, Fragment fragment, String tag, String lastTag, boolean addToBackStack ) {
	        FragmentManager fragmentManager = getSupportFragmentManager();
	        FragmentTransaction transaction = fragmentManager.beginTransaction();
	
	        if ( lastTag != null ) {
	            Fragment lastFragment = fragmentManager.findFragmentByTag( lastTag );
	            if ( lastFragment != null ) {
	                transaction.hide( lastFragment );
	            }
	        }
	
	        if ( fragment.isAdded() ) {
	            transaction.show( fragment );
	        }
	        else {
	            transaction.add( resId, fragment, tag );//.setBreadCrumbShortTitle( tag );
	        }
	
	        if ( addToBackStack ) {
	            transaction.addToBackStack( tag );
	        }
	
	        transaction.commit();
	}
	

} // end class
