package utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapCache implements ImageCache{
	public LruCache<String, Bitmap> cache;
	public int max=10*1024*1024; //���Ļ����С10��
	
	public BitmapCache(){
		cache = new LruCache<String, Bitmap>(max){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// TODO Auto-generated method stub
				return value.getRowBytes()*value.getHeight();
			}
		};
	}
	@Override
	public Bitmap getBitmap(String url) {
		return cache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		cache.put(url, bitmap);
	}

}
