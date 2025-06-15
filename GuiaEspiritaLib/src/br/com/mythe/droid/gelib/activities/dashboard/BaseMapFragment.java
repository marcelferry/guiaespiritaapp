package br.com.mythe.droid.gelib.activities.dashboard;
import org.jredfoot.sophielib.transacao.Transacao;
import org.jredfoot.sophielib.transacao.TransacaoTask;
import org.jredfoot.sophielib.util.NetUtils;
import org.jredfoot.sophielib.util.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.mythe.droid.gelib.App;
import br.com.mythe.droid.gelib.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public abstract class BaseMapFragment extends Fragment {
    private GoogleMap mMap;
    private TransacaoTask task;
    private static final String TAG = "GE";

    abstract protected int getLayoutId();
    abstract protected int getMapaId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setHasOptionsMenu(true);
    	setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view;
        if (container == null) {
           return null;
       }
        view = (RelativeLayout) inflater.inflate(getLayoutId(), container, false);

       setUpMapIfNeeded(); // For setting up the MapFragment

        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	inicializar(savedInstanceState);
    	
		popularTela();
    	
    }
    
    protected abstract void inicializar(Bundle savedInstanceState);
	protected abstract void popularTela();
    
    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    
    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(getMapaId())).setRetainInstance(true);
        mMap = ((SupportMapFragment)  getActivity().getSupportFragmentManager().findFragmentById(getMapaId())).getMap();
        if (mMap != null) {
            startDemo();
        }
    }

    /**
     * Run the demo-specific code.
     */
    protected abstract void startDemo();

    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }
    
    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then 
     **** app will crash ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (mMap != null) {
//        	Log.d("GELIB", "Destruindo View MAPA");
//        	Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(getMapaId());
//        	if(fragment != null){
//        		getActivity().getSupportFragmentManager().beginTransaction()
//        			.remove(fragment).commitAllowingStateLoss(); 
//        	}
//            mMap = null;
//        }
    }
    
    public void setTitleFromActivityLabel (int textViewId)
	{
	    TextView tv = (TextView) getView().findViewById (textViewId);
	    if (tv != null) tv.setText (getActivity().getTitle ());
	} // end setTitleText

	public void toast (String msg)
	{
	    Toast.makeText (getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
	} // end toast

	public void trace (String msg) 
	{
	    Log.d("Demo", msg);
	    toast (msg);
	}
    
    protected boolean isLite() {
		return getActivity().getPackageName().toLowerCase().contains("lite");
	}
    
    protected App getGlobals() {
		return (App) getActivity().getApplication();
	}
	
	protected void setTitle(String string) {
		getActivity().setTitle(string);
	}
	
	public void startTransacao(Transacao transacao){
		boolean redeOk = NetUtils.isNetworkConnected(getActivity());
		if(redeOk){
			task = new TransacaoTask(getActivity(), transacao, R.string.aguarde , R.string.app_name);
			task.execute();
		} else {
			Utils.alertDialog(getActivity(), R.string.erro_conexao_indisponivel, R.string.app_name);
		}
	}
	
	public void startFragment(Fragment fragment, Bundle args, String tag){
	    fragment.setArguments(args);
	
	    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
	    FragmentTransaction ft = fragmentManager.beginTransaction();
	    if(tag != null){
	    	ft.replace(R.id.content_frame, fragment, tag);
	    } else {
	    	ft.replace(R.id.content_frame, fragment);
	    }
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
	}
}