package br.com.mythe.droid.common.view;

import java.math.BigDecimal;
import java.text.NumberFormat;

import android.text.Editable;
import android.text.TextWatcher;

public class CurrencyTextWatcher implements TextWatcher {

	boolean mEditing;

	public CurrencyTextWatcher() {
		mEditing = false;
	}

	public synchronized void afterTextChanged(Editable s) {
		if (!mEditing) {
			mEditing = true;

			String digits = s.toString().replaceAll("\\D", "");
			NumberFormat nf = NumberFormat.getNumberInstance();
			
			//nf.setCurrency(Currency.getInstance("BRL"));
			
			nf.setGroupingUsed(true);
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);
			
			try {
				BigDecimal parsed = new BigDecimal(digits).setScale(2,BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100),BigDecimal.ROUND_FLOOR);                
				String formato = nf.format(parsed);
				s.replace(0, s.length(), formato);
			} catch (NumberFormatException nfe) {
				s.clear();
			}

			mEditing = false;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}
}