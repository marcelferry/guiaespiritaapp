package br.com.mythe.droid.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class HttpHelper {
	private static final String TAG = "Http";
	public static boolean LOG_ON = false;

	public static String doGet(String url, String charset) throws IOException {
		if (LOG_ON) {
			Log.d(TAG, ">> Http.doGet: " + url);
		}
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.connect();
		InputStream in = conn.getInputStream();
		String s = IOUtils.toString(in, charset);
		if (LOG_ON) {
			Log.d(TAG, "<< Http.doGet: " + s);
		}
		in.close();
		conn.disconnect();
		return s;
	}

	public static Bitmap doGetBitmap(String url) throws IOException {
		if (LOG_ON) {
			Log.d(TAG, ">> Http.doGet: " + url);
		}
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.connect();
		InputStream in = conn.getInputStream();
		byte[] bytes = IOUtils.toBytes(in);
		if (LOG_ON) {
			Log.d(TAG, "<< Http.doGet: " + bytes);
		}
		in.close();
		conn.disconnect();
		Bitmap bitmap = null;
		if(bytes != null) {
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
		return bitmap;
	}
}