package br.com.mythe.droid.common.view;

import java.util.HashMap;

import android.graphics.Bitmap;

public class CacheImagem {

	private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	public Bitmap get(String url) {
		return cache.get(url);
	}

	public void put(String url, Bitmap bitmap) {
		cache.put(url, bitmap);
	}
}