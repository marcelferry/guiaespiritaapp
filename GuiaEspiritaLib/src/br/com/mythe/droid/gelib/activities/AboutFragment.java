package br.com.mythe.droid.gelib.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.DashboardActivity;

/**
 * This is the About activity in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class AboutFragment extends Fragment 
{
	
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_about, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    	
	    getActivity().setTitle(R.id.title_text);
	    
	    ImageView image = (ImageView) getView().findViewById(R.id.imageView1);
	    image.setAlpha(32);
	}
    
} // end class
