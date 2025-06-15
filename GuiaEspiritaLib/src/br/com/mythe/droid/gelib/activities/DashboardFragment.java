package br.com.mythe.droid.gelib.activities;

import org.jredfoot.sophielib.util.AdMobsUtil;
import org.jredfoot.sophielib.util.MapUtils;
import org.jredfoot.sophielib.util.NetUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;

public class DashboardFragment extends Fragment implements OnClickListener {

	private Button btListaEstados;
	private Button btFavoritos;
	private Button btProximos;
	private Button btMapasProximos;
	private Button btCountry;
	private Button btPreferencias;
	private Button btAbout;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_home, null);

		btListaEstados = (Button) view.findViewById(R.id.home_btn_lista_item);
		btFavoritos = (Button) view.findViewById(R.id.home_btn_favoritos);
		btProximos = (Button) view.findViewById(R.id.home_btn_items_proximos);
		btMapasProximos = (Button) view.findViewById(R.id.home_btn_mapa_proximos);
		btCountry = (Button) view.findViewById(R.id.home_btn_country);
		btPreferencias = (Button) view.findViewById(R.id.home_btn_preferences);
		btAbout = (Button) view.findViewById(R.id.home_btn_about);

		btListaEstados.setOnClickListener(this);
		btFavoritos.setOnClickListener(this);
		btProximos.setOnClickListener(this);
		btMapasProximos.setOnClickListener(this);
		btCountry.setOnClickListener(this);
		btPreferencias.setOnClickListener(this);
		btAbout.setOnClickListener(this);
		
		return view;
	}
	
	 @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    	
    	getActivity().setTitle("Dashboard");

    	if(isLite()){
    		AdMobsUtil.addAdView(getActivity());
    	}
    	
    }
	


	@Override
	public void onClick(View v) {
		Fragment fragment = null;
		String fragmentTag = null;
		boolean frag = true;
		int id = v.getId();
		if (id == R.id.home_btn_lista_item) {
			fragmentTag = HomeActivity.FRAGMENT_LISTA_ESTADOS;
			fragment = new ListaEstadosFragment();			
		} else if (id == R.id.home_btn_favoritos) {
			fragmentTag = HomeActivity.FRAGMENT_LISTA_FAVORITOS;
			fragment = new ListaFavoritosFragment();
		} else if (id == R.id.home_btn_items_proximos) {
			if (!MapUtils.isGPSActive(getActivity())) {
				abrirDialogGPS();
			} else {
				fragmentTag = HomeActivity.FRAGMENT_LISTA_PROXIMOS;
				fragment = new ListaProximosFragment();
			}
		} else if (id == R.id.home_btn_mapa_proximos) {
			if (!MapUtils.isGPSActive(getActivity())) {
				abrirDialogGPS();
			} else {
				if (NetUtils.isNetworkConnected(getActivity())) {
					fragmentTag = HomeActivity.FRAGMENT_RADAR;
					fragment = new RadarProximidadeFragment();
				} else {
					toast("Não há conexão no momento. Tente mais tarde.");
				}
			}
		} else if (id == R.id.home_btn_country) {
			fragmentTag = HomeActivity.FRAGMENT_LISTA_PAISES;
			fragment = new ListaPaisesFragment();
		} else if (id == R.id.home_btn_preferences) {
			startActivity(new Intent(getActivity(), Preferences.class));
			frag = false;
		} else if (id == R.id.home_btn_about) {
			fragmentTag = HomeActivity.FRAGMENT_SOBRE;
			fragment = new AboutFragment();
		} else {
			
		}
		
		if(frag){
			Bundle args = new Bundle();
			startFragment(fragment, args, fragmentTag);
		}
	}
	
	public void abrirDialogGPS() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Habilitar Localização");
		alert.setMessage("Você deve ativar os serviços de localização para utilizar essa funcionalidade.");

		alert.setPositiveButton("Habilitar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});

		alert.setNegativeButton("Agora não",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}
	
	public void toast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	} 
	
	protected boolean isLite() {
		return getActivity().getPackageName().toLowerCase().contains("lite");
	}
	
	public void startFragment(Fragment fragment, Bundle args, String fragmentTag){
	    fragment.setArguments(args);
	
	    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
	    FragmentTransaction ft = fragmentManager.beginTransaction();
	    ft.replace(R.id.content_frame, fragment, fragmentTag);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
	}

}
