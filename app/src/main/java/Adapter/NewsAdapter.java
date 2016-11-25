package Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xx.htmlproject.R;

import java.util.List;

/**
 * Created by XX on 2016/4/14.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> NewsNames;
    private List<String> LogoUrl;

    public interface RecyItemOnclick
    {
        void onItemClick(View view, int postion);
        void onItemLongClick(View view,int postion);
    }
    private  RecyItemOnclick recyitemonclick;

    public void setRecyItemOnclick(RecyItemOnclick Listener){
        this.recyitemonclick=Listener;
    }

    public NewsAdapter(Context mContext ,List<String> NewsNames,List<String> LogoUrl) {
        this.mContext = mContext;
        this.NewsNames=NewsNames;
        this.LogoUrl=LogoUrl;
        mLayoutInflater=LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=mLayoutInflater.inflate(R.layout.select_news_item_layout,parent,false);
        return new MyViewHolder(view,recyitemonclick);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.tv_news_item_title.setText(NewsNames.get(position));
            Glide.with(mContext).load(LogoUrl.get(position)).thumbnail(0.1f).into(holder.iv_news_item_image);
    }



    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        TextView tv_news_item_title;
        ImageView iv_news_item_image;
        RecyItemOnclick recyItemOnclick;
        public MyViewHolder(View itemView,RecyItemOnclick recyItemOnclick) {
            super(itemView);
            tv_news_item_title= (TextView) itemView.findViewById(R.id.id_news_item_title);
            iv_news_item_image= (ImageView) itemView.findViewById(R.id.id_news_item_image);
            this.recyItemOnclick=recyItemOnclick;
            itemView.setOnClickListener(this);//设置点击监听
            itemView.setOnLongClickListener(this);//设置长点击监听
        }





        @Override
        public void onClick(View view) {
            if (recyItemOnclick!=null)
            {
                int position=getLayoutPosition();
                recyItemOnclick.onItemClick(view,position);

            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(recyItemOnclick!=null)
            {
                int position=getLayoutPosition();
                recyItemOnclick.onItemLongClick(view,position);
            }
            return false;
        }

    }
    @Override
    public int getItemCount() {
        return NewsNames.size();
    }
}
