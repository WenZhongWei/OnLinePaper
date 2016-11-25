package com.example.volleycache;

import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.base.ImageUrl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyAdapter extends BaseAdapter{
	
	List<ImageUrl> list;
	Context context;
	//图片加载器
	ImageLoader imageLoader;
	public  MyAdapter(Context context,List<ImageUrl> Urllist){
		this.context = context;
		this.list = Urllist;
		RequestQueue queue = MyApplication.getRequestQueue();
		//实例化图片加载器
		imageLoader = new ImageLoader(queue, MyImageCache.getImageCache(context));
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item, null);
			holder = new ViewHolder();
			holder.imageView= (NetworkImageView) convertView.findViewById(R.id.imageView);
			convertView.setTag(holder);
			
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//设置加载中的图片	
		holder.imageView.setDefaultImageResId(R.drawable.ic_launcher);
		//设置加载失败的图片
		holder.imageView.setErrorImageResId(R.drawable.ic_launcher);
		//设置要下载的图片和图片加载器
		holder.imageView.setImageUrl(list.get(position).getBannerImage(), imageLoader);
		
		return convertView;
	}
	
	private  class ViewHolder {
		//使用Volley自带的NetworkImageView控件；
		NetworkImageView imageView;
		
	}

}
	
