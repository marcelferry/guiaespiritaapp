package br.com.mythe.droid.gelib.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jredfoot.sophielib.util.AdMobsUtil;
import org.jredfoot.sophielib.util.MapUtils;
import org.jredfoot.sophielib.util.NetUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseListFragment;
import br.com.mythe.droid.gelib.adapter.CasasAdapter;
import br.com.mythe.droid.gelib.constants.GEConst;
import br.com.mythe.droid.gelib.cursor.ItemListaCursorAdapter;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.dialog.ListClickDialog;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;
import br.com.mythe.droid.gelib.vo.CasaVO;

public class ListaProximosFragment extends BaseListFragment implements
		OnItemLongClickListener {
	
	private ProgressDialog mProgressDialog;
	private AlertDialog mListClickDialog;
	private static final int PROGRESS_DIALOG = 0;
	
	private static final int NETWOTK_ERROR_WAIT = -1;
	
	LocationManager locationManager = null;
	Criteria criteria = null;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case NETWOTK_ERROR_WAIT: 
					Toast.makeText(getActivity(), "Erro ao sincronizar conteúdo Web, verifique sua conexão ou tente novamente mais tarde.", Toast.LENGTH_LONG).show();
					break;
			}
		}
	};
	public static Handler mStaticHandler = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_lista_proximos);
	}
	
	@Override
	protected void inicializar(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void popularTela() {
	
		setTitle("Entidades Mais Proximas");
		setTitleFromActivityLabel(R.id.title_text);
		mStaticHandler = mHandler;

	    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

	    // Define a set of criteria used to select a location provider.
	    criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    criteria.setAltitudeRequired(false);
	    criteria.setBearingRequired(false);
	    criteria.setCostAllowed(true);
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
		
	    if(isLite()){
			AdMobsUtil.addAdView(getActivity());
		}
    	
	}
	
	private final LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	    	toast("GPS Atualizado");
	    	updateWithNewLocation(location);
	    }
	   
	    public void onProviderDisabled(String provider){
	    	updateWithNewLocation(null);
	    }

	    public void onProviderEnabled(String provider) {}

	    public void onStatusChanged(String provider, int status, Bundle extras) {}
	  };
	
	@Override
	public void onStart() {
		super.onStart();
		// Find a Location Provider to use.
	    String provider = locationManager.getBestProvider(criteria, true);

	    // Update the GUI with the last known position.
	    Location location = locationManager.getLastKnownLocation(provider);
	    updateWithNewLocation(location);

	    // Register the LocationListener to listen for location changes
	    // using the provider found above.
	    locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
	}
	
	private void updateWithNewLocation(Location location) {
		atualizarCasas(location);
	}

	@Override
	public void onResume() {
		super.onResume();		
		String provider = locationManager.getBestProvider(criteria, true);
		if(provider != null)
			locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(locationListener);
	}
	
	@Override
	public void onStop() {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			getActivity().dismissDialog(PROGRESS_DIALOG);
		super.onStop();
		locationManager.removeUpdates(locationListener);
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog d = null;
		if (id == PROGRESS_DIALOG) {
			createProgressDialog();
			d = mProgressDialog;
		}
		return d;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Integer idItem = null;
		Object teste = getListAdapter().getItem( position );
		if(teste instanceof CasaVO){
			CasaVO c = (CasaVO) teste;
			idItem = (int)c.id_casa;
		} else if (teste instanceof Cursor) {
			Cursor c = (Cursor) teste;
			idItem = c.getInt(1);
		}

		Intent i = new Intent(getActivity(), ItemDetalheActivity.class);
		i.putExtra("id_centro", idItem);
		startActivity(i);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> l, View view, int position,
			long id) {
		Integer idItem = null;
		String site = null;
		Object teste = getListAdapter().getItem( position );
		if(teste instanceof CasaVO){
			CasaVO c = (CasaVO) teste;
			idItem = (int)c.id_casa;
			site = c.site;
		} else if (teste instanceof Cursor) {
			Cursor c = (Cursor) teste;
			idItem = c.getInt(1);
			site = c.getString(c.getColumnIndex(Casas.SITE));
		}
		
		
		AlertDialog.Builder builder = null;	
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
	
	
	private void atualizarCasas(Location origem){
		//if(NetUtils.isOnline(getActivity())){
		//	new SyncProgressTask().execute(new Void[]{});
		//} else {
			
			String[] projection = new String[]{	
					Casas._ID,
					Casas.COD_CASA,
					Casas.NOME,
					Casas.ENDERECO,
					Casas.BAIRRO,
					Casas.CIDADE,
					Casas.SITE,
					Casas.LAT,
					Casas.LNG,
					Casas.TYPE
				};
			
			String[] displayFields = new String[]{
					//Casas.COD_CASA,
					Casas.NOME,
					Casas.ENDERECO,
					Casas.BAIRRO
				};
			
			
			int[] displayViews = new int[]{
					//R.id.codigo,
					R.id.nome,
					R.id.endereco,
					R.id.bairro
				};
			
				if(origem != null){			
					Cursor cur = getActivity().managedQuery(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), projection, null, null, " (lat - "+ origem.getLatitude() +") * (lat - "+ origem.getLatitude() +")  + (lng - " + origem.getLongitude() + " ) * (lng - " + origem.getLongitude() + " ) LIMIT 10");		
					//setListAdapter(new CasasListaCursor(getActivity(), R.layout.lista_casas_item, cur, displayFields, displayViews));
					setListAdapter(new ItemListaCursorAdapter(getActivity(), cur, true));
				}
		//}			
	}
	
	private List<CasaVO> carregaCasasEspiritas() throws Exception {
		
		Location origem = MapUtils.getMyLocation(getActivity());
		
		Map<String, String> parametros = new HashMap<String, String>();
		parametros.put(GEConst.PAR_PROXIMOS_LAT_FROM, String.valueOf( origem.getLatitude() ));
		parametros.put(GEConst.PAR_PROXIMOS_LNG_FROM, String.valueOf( origem.getLongitude() ));
		
		String caminho = NetUtils.generateGetUrl(GEConst.PROXIMOS, parametros);
		
		Document doc = NetUtils.getXmlFromService(caminho);
		
		int casasSize = doc.getElementsByTagName("entidade").getLength();

		List<CasaVO> casas = new ArrayList<CasaVO>();
		
		for (int i = 0; i < casasSize; i++) {
			Element casa = (Element) doc.getElementsByTagName("entidade").item(i);

			CasaVO casaVO = new CasaVO();
			casaVO.id_casa = new Integer(casa.getAttribute("codigo"));			
			casaVO.entidade = casa.getAttribute("name");
			casaVO.endereco = casa.getAttribute("address");
			casaVO.bairro = casa.getAttribute("bairro");
			casaVO.distance = casa.getAttribute("distance");
			casas.add(casaVO);
		}
		
		return casas;

	}
	
	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage("Localizando, aguarde.");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
	}
	
	class SyncProgressTask extends AsyncTask<Void, Integer, List<CasaVO>> {
	    @Override
	    protected List<CasaVO> doInBackground(Void... params) {
	    	
	    	List<CasaVO> casas = new ArrayList<CasaVO>(); 
	    	
	    	try{
				casas = carregaCasasEspiritas();
			} catch (Exception e) {
				e.printStackTrace();
				Message m = Message.obtain(mHandler, NETWOTK_ERROR_WAIT );
				mHandler.sendMessage(m);
				
			}
			
	    	return casas;
	    }

	    @Override
	    protected void onPreExecute() {
	        getActivity().showDialog(PROGRESS_DIALOG);
	    }

	    
	    @Override
	    protected void onPostExecute(List<CasaVO> result) {
	        getActivity().removeDialog(PROGRESS_DIALOG);
	        
	        List<CasaVO> casas = result;
			
			setListAdapter(new CasasAdapter(getActivity(), R.layout.lista_casas_item, casas));
	    }
	}

	
}
