package br.com.mythe.droid.gelib.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import br.com.mythe.droid.gelib.R;

public class NovoItemMapaDialog extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	setContentView(R.layout.activity_base);
    	
    	if(savedInstanceState == null){
    		NovoItemMapaFragment fragClientesLista = new NovoItemMapaFragment();
    		
    		Bundle args = getIntent().getExtras();
    		
    		fragClientesLista.setArguments(args);
    		
    		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
    		t.add(R.id.layout1, fragClientesLista);
    		t.commit();
    	}
    	
    }
    
}