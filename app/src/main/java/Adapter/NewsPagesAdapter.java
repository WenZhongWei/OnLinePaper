package Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.example.xx.htmlproject.R;

import java.util.ArrayList;

import GsonBean.SearchPages;

/**
 * Created by XX on 2016/4/14.
 */
public class NewsPagesAdapter extends RecyclerView.Adapter<NewsPagesAdapter.MyViewHolder> {
    private SearchPages searchPages;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private RequestQueue mRequestQueue;
    private ArrayList imagesURL;

    //点击监听接口
    public interface RecyItemOnclick
    {
        void onItemClick(View view, int postion);
    }
    private  RecyItemOnclick recyitemonclick;

    public void setRecyItemOnclick(RecyItemOnclick Listener){
        this.recyitemonclick=Listener;
    }


    public NewsPagesAdapter(Context Context , SearchPages searchPages ,ArrayList<String> imagesURL,RequestQueue RequestQueue) {
        this.searchPages =searchPages;
        this.mContext = Context;
        this.imagesURL=imagesURL;
        this.mRequestQueue=RequestQueue;
        mLayoutInflater=LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=mLayoutInflater.inflate(R.layout.pages_item_layout,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view,recyitemonclick);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tv_pages_item_title.setText("第"+(position+1)+"版");//取出数据并赋值
        /***searchpages对象只有一个
         * 下面是通过NetworkImageView控件进行图片加载
         * **/
        setImage(holder,position);

    }

    private void setImage(final MyViewHolder holder, int position) {


//        ImageLoader imageLoader=new ImageLoader(mRequestQueue,new BitmapCache());//设置缓存
//        holder.iv_pages_item_image.setDefaultImageResId(android.R.drawable.ic_menu_gallery);//设置加载中的图片
//        holder.iv_pages_item_image.setErrorImageResId(R.mipmap.error_image);//加载错误图片
//
//        holder.iv_pages_item_image.setImageUrl(String.valueOf(imagesURL.get(position)), imageLoader);//设置图片
//        System.out.println("这是第"+position+"个"+"URL为"+imagesURL.get(position));
        Glide.with(mContext)
                .load(imagesURL.get(position))//下载地址
                .thumbnail(0.1f)//加载时显示缩略图
                .error(R.mipmap.download_error)//下载失败显示图
                .into(holder.iv_pages_item_image);

    }


    @Override
    public int getItemCount() {
        int pagescount=Integer.parseInt(searchPages.getData().get(0).getPage());//返回页数
        return pagescount;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_pages_item_title;
        ImageView iv_pages_item_image;
        RecyItemOnclick recyItemOnclick;
        public MyViewHolder(View itemView,RecyItemOnclick recyItemOnclick) {
            super(itemView);
            tv_pages_item_title= (TextView) itemView.findViewById(R.id.id_pages_item_title);
            iv_pages_item_image= (ImageView) itemView.findViewById(R.id.id_pages_item_image);
            this.recyItemOnclick=recyItemOnclick;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (recyItemOnclick!=null)
            {
                int positon=getLayoutPosition();
                recyItemOnclick.onItemClick(view,positon);
            }
        }
    }


}
