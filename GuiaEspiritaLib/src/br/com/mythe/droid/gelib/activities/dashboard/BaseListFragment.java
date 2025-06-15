package br.com.mythe.droid.gelib.activities.dashboard;

import java.util.List;

import org.jredfoot.sophielib.transacao.Transacao;
import org.jredfoot.sophielib.transacao.TransacaoTask;
import org.jredfoot.sophielib.util.NetUtils;
import org.jredfoot.sophielib.util.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.AboutFragment;
import br.com.mythe.droid.gelib.activities.HomeActivity;
import br.com.mythe.droid.gelib.activities.ListaEstadosFragment;
import br.com.mythe.droid.gelib.activities.ListaFavoritosFragment;
import br.com.mythe.droid.gelib.activities.ListaProximosFragment;
import br.com.mythe.droid.gelib.activities.MapaProximosActivity;
import br.com.mythe.droid.gelib.activities.Preferences;
import br.com.mythe.droid.gelib.login.LoginActivity;
import br.com.mythe.droid.gelib.vo.ListaObjetos;

public abstract class BaseListFragment extends ListFragment {

	protected List lista;  
	private TransacaoTask task;
	private static final String TAG = "GE";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setHasOptionsMenu(true);
    	setRetainInstance(true);
    }
   
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState, int layout) {
    	
    	View view = inflater.inflate(layout, null);
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	inicializar(savedInstanceState);
    	
		if (savedInstanceState != null) {
			// Recuperamos a lista de lista salva pelo onSaveInstanceState(bundle)
			ListaObjetos lista = (ListaObjetos) savedInstanceState.getSerializable(ListaObjetos.KEY);
			Log.i(TAG,"Lendo estado: savedInstanceState(lista)");
			this.lista = lista.lista;
		}
		
		popularTela();
    	
    }
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
		Log.i(TAG,"Salvando Estado: onSaveInstanceState(bundle)");
		// Salvar o estado da tela
		outState.putSerializable(ListaObjetos.KEY, new ListaObjetos(lista));
    }
    
	protected abstract void inicializar(Bundle savedInstanceState);
	protected abstract void popularTela();

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
	
	public void abrirDialogLogin(final Context context, final boolean back) {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				context);

		alert.setTitle(R.string.title_naologado);
		alert.setMessage(R.string.msgNaoLogado);
		
		alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(context,
						LoginActivity.class);
				intent.putExtra("back", back);
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
	
	protected boolean isLite() {
		return getActivity().getPackageName().toLowerCase().contains("lite");
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
