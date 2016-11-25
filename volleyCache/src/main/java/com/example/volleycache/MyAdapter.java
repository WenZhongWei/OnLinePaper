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
	//ͼƬ������
	ImageLoader imageLoader;
	public  MyAdapter(Context context,List<ImageUrl> Urllist){
		this.context = context;
		this.list = Urllist;
		RequestQueue queue = MyApplication.getRequestQueue();
		//ʵ����ͼƬ������
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
		//���ü����е�ͼƬ	
		holder.imageView.setDefaultImageResId(R.drawable.ic_launcher);
		//���ü���ʧ�ܵ�ͼƬ
		holder.imageView.setErrorImageResId(R.drawable.ic_launcher);
		//����Ҫ���ص�ͼƬ��ͼƬ������
		holder.imageView.setImageUrl(list.get(position).getBannerImage(), imageLoader);
		
		return convertView;
	}
	
	private  class ViewHolder {
		//ʹ��Volley�Դ���NetworkImageView�ؼ���
		NetworkImageView imageView;
		
	}

}
	
