package br.com.mythe.droid.common.view;

import java.io.IOException;

import br.com.mythe.droid.common.util.HttpHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DownloadImageView extends ImageView {

	public DownloadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DownloadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DownloadImageView(Context context) {
		super(context);
	}

	public Bitmap setUrl(Context context, String url) throws IOException {
		final Bitmap bitmap = HttpHelper.doGetBitmap(url);
		if(bitmap != null) {
			post(new Runnable() {
				@Override
				public void run() {
					// Roda na Thread principal
					setImageBitmap(bitmap);
				}
			});
		}
		return bitmap;
	}
}