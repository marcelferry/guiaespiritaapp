package br.com.mythe.droid.common.util;

import android.app.Activity;
import android.widget.LinearLayout;
import br.com.mythe.droid.gelib.R;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class AdMobsUtil {
	
	public static final String AD_UNIT_ID = "a14f175ea07dd89";
	
	public static void addAdView(Activity activity){
		LinearLayout adMobLayout = (LinearLayout) activity.findViewById(R.id.adMob);
		AdView adView = new AdView(activity, AdSize.BANNER, AD_UNIT_ID);
		adMobLayout.addView(adView);
		AdRequest request = new AdRequest();    	
		adView.loadAd(request);
	}


	
}
