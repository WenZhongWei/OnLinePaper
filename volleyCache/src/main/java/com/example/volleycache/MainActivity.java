package com.example.volleycache;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.base.Banner;
import com.example.base.ImageUrl;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
	String TAG = "MainActivity";
	ListView listView = null;
	Button button = null;
	
	RequestQueue queue = null;
	String stringUrl = "http://112.124.118.133:9065/ssgApp/getBannerList"; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
        //获取请求队列
        queue = MyApplication.getRequestQueue();
        volleyGet(queue);
        
        
        
        
    }
	private void volleyGet(final RequestQueue queue) {
		
		StringRequest stringRequest = new StringRequest(stringUrl,
        		new Listener<String>() {
			
					@Override
					public void onResponse(String response) {
						Log.i(TAG, "请求成功");
						
						addAdapter(analysisJson(response));
					}					
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						//请求失败 则加载缓存 实现离线体验
						Log.i("MainActivity", "请求失败");
						Log.i("MainActivity", "加载缓存");
						if(queue.getCache().get(stringUrl)!=null){
							addAdapter(analysisJson(new String(queue.getCache().get(stringUrl).data).toString()));
							
						}
					}
				});
		queue.add(stringRequest);
	}
	
	protected void addAdapter(List<ImageUrl> list) {
		
		MyAdapter myAdapter = new MyAdapter(getApplicationContext(), list);
		listView.setAdapter(myAdapter);
		
	}
	
	
	
	private List<ImageUrl> analysisJson(String string) {
		// TODO Auto-generated method stub
		try {
			Gson gson = new Gson();
			Banner banner=gson.fromJson(string, Banner.class);
			List<ImageUrl> Urllist = new ArrayList<ImageUrl>();
			Urllist = banner.getData();
			Log.i(TAG, "解析成功");
			return Urllist;

		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "解析失败");
		}
		return null;
	}
	
	private void initView() {
		listView = (ListView) findViewById(R.id.listview);
		button = (Button) findViewById(R.id.button);
		
	}
	



}
