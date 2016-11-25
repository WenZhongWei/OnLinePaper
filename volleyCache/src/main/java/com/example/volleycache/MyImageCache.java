package com.example.volleycache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;




 

public class MyImageCache implements ImageCache{
	 /** 
     * �ڴ滺�棺��ͼƬ�ݴ����ڴ��У� �������ã�.Ŀ�ģ�Ϊ����������������ȣ�ͬʱ��ʡ�û������� 
     *  
     * ���ػ��棺��ͼƬ�־û������õı������ⲿ�洢���ʣ���Ŀ�ģ��ڳ���û������ʱ����Ȼ��������ʾ 
     *  
     * ʹ��Volley������ʵ��ͼƬ��3������ 
     *  
     * ��1��.LruCache ���ڴ滺�棩 L1 
     *  
     * androidϵͳ��ÿһ��Ӧ�ó�����һ���̶���С���ڴ���䣨16M��32M��,��Щ�洢�ռ���һ���� 
     * ��ר�����ڴ洢ͼƬ�������򲻶����ڴ��м���ͼƬ����ʱ���ﵽϵͳΪͼƬ������ڴ��ֵ��ϵͳ�ͻ� ��OOM���󣬱�ϵͳǿ���˳� 
     *  
     * ��Android3.0֮ǰ�������ж�ͼƬ���ڴ滺�涼�ǿ��������п��Ƶģ����ַ������׵���OOM. 
     *  
     * ��Ϊʵ�ʿ���������,Google��3.0����LruCache��ʵ���ڴ�Ĺ��� 
     *  
     * LruCache��һ�����ڴ��л������ݵ��������ص㣺1������ָ��������������2������Lru���������ʹ�ã� �㷨��ʵ�����������ݵĹ��� 
     *  
     * ��2��.SoftReference ���ڴ滺�棩 L2 
     *  
     * ��3��.�ⲿ�洢���� ���ⲿ���棩 L3 
     */ 
	
	private static MyImageCache myImageCache;
	private static Context context;
	
	private LruCache<String, Bitmap> lruCache;//һ������
	private HashMap<String, SoftReference<Bitmap>> softMap;//��Ŷ��������Map����
	
	public void ab(){
		softMap.clear();
	}
	public static MyImageCache getImageCache(Context context){
		if (myImageCache == null) {
			myImageCache = new MyImageCache(context);
		}
		return myImageCache;
		
	}
	
	private MyImageCache(Context context){
		this.context = context;
		//��Ŷ��������map����
		softMap = new HashMap<String, SoftReference<Bitmap>>();
		//��ȡ��ҪLrucache���ڴ��С��һ�������ڴ��1/4��1/8��
		int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
		//����һ������ǿ���ö���
		lruCache = new LruCache<String, Bitmap>(maxSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// ����ÿ��ͼƬռ�õ��ڴ��С 
				return value.getRowBytes() * value.getHeight();
			}
			
			//��ʾָ���������б��Ƴ���ͼƬ���ͷŷ�ʽ����������д�������Բ�����д��
			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
				
				// evicted: true������LruCache�����������߳�ͼƬ����  
                // evicted: false���������put<K,V>��remove(K)�����Ƴ�ͼƬ����  
                if (evicted) {// true ���oldValue����������  
                    // ʵ�ֶ������棨L2��  
                    SoftReference<Bitmap> reference = new SoftReference<Bitmap>(  
                            oldValue);  
                    softMap.put(key, reference);  
                }
			}	
		};
	}
	
	/** 
     * ��ȡ����ͼƬ���� 
     *  
     * @param url 
     *            ��ȡͼƬ�����ӣ���key 
     */  
	@Override
	public Bitmap getBitmap(String url) {
		//�ȶ�ȡһ�������ͼƬ
		Bitmap bitmapL1 = lruCache.get(url);
		if (bitmapL1 !=null) {//��ʾһ��������������
			return bitmapL1;
			
		}
		//һ��û������ʱ���ٶ�ȡ���������ͼƬ
		SoftReference<Bitmap> soft = softMap.get(url);
		if (soft != null) {
			Bitmap bitmapL2 = soft.get();
			if (bitmapL2 != null) {//��ʾ����������������
				//���°�ʹ�õ�ͼƬ����ǿ������
				lruCache.put(url, bitmapL2);
				return bitmapL2;
			}
		}
		File cacheFile = getCacheFile(context);
		return readCache(url,cacheFile);
	}

	 /** 
     * ����ͼƬ 
     *  
     * @param url 
     *            ��ǰͼƬ��Ӧ��url(������Ψһ��ʶKey) 
     * @param bitmap 
     *            ��Volleyͨ��ImageLoader���ص�ͼƬ 
     */  
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		//ʵ��һ�����棨L1��
		lruCache.put(url, bitmap);
		//ʵ���������棨L3��
		File cacheFile = getCacheFile(context);
		WriteCache(bitmap, cacheFile, url);
		
	}
	
    /** 
     * ��ͼƬ����д�뻺�棬�����浽���ػ��� 
     *  
     * @param bitmap 
     * @param cacheFile 
     * @param str 
     */  
	private void WriteCache(Bitmap bitmap, File cacheFile, String url) {
		String[] str = url.split("/");
		try {
			FileOutputStream fos = new FileOutputStream(cacheFile.getAbsolutePath()
					+ File.separator + str[str.length - 1]);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/** 
     * ��ȡ���ػ����ͼƬ 
     *  
     * @param url 
     * @param cacheFile 
     * @return Bitmap 
     */
	private Bitmap readCache(String url, File cacheFile) {
		String[] str = url.split("/");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(cacheFile.getAbsolutePath()
					+ File.separator + str[str.length - 1]);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null; 
	}

	/**
	 * ��ȡCache�ļ�
	 */
	private File getCacheFile(Context context){
		File cacheFile = null;
		//�ж�sd���Ƿ����
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//��sd���ϴ��������ļ���
			File sdCache = context.getExternalCacheDir();
			cacheFile = sdCache;
		}else {
			//���ֻ��Դ��Ĵ洢�ռ䴴�������ļ���
			File internalCache = context.getCacheDir();
			cacheFile = internalCache;
		}
		
		return cacheFile;
		
	}
	
}