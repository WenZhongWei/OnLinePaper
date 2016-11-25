package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xx.htmlproject.R;

import GsonBean.NewsPaper;

/**
 * Created by XX on 2016/5/6.
 */
public class SearchAllNewsPaperAdapter extends RecyclerView.Adapter<SearchAllNewsPaperAdapter.MyViewHolder>{
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private NewsPaper mNewsPaper;

    public interface RecyItemOnclick
    {
        void onItemClick(View view, int postion);
    }
    private  RecyItemOnclick recyitemonclick;

    public void setRecyItemOnclick(RecyItemOnclick Listener){
        this.recyitemonclick=Listener;
    }

    public SearchAllNewsPaperAdapter(Context mContext ,NewsPaper mNewsPaper) {
        this.mContext = mContext;
        this.mNewsPaper=mNewsPaper;
        mLayoutInflater=LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=mLayoutInflater.inflate(R.layout.search_all_newspaper_item_layout,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view,recyitemonclick);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_name.setTypeface(Typeface.MONOSPACE);
        holder.tv_name.setText(mNewsPaper.getData().get(position).getName());
        holder.tv_type.setTextColor(Color.parseColor("#1982b6"));
        holder.tv_type.setText(mNewsPaper.getData().get(position).getType());
//        Date currentTime =new Date();
//        SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy.MM.dd");
//        String time=timeFormat.format(currentTime);
        holder.tv_time.setHint("查找全部");

    }


    @Override
    public int getItemCount() {
        return mNewsPaper.getData().size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name;
        TextView tv_type;
        TextView tv_time;
        RecyItemOnclick recyItemOnclick;
        public MyViewHolder(View itemView,RecyItemOnclick recyItemOnclick) {
            super(itemView);
            tv_name= (TextView) itemView.findViewById(R.id.id_search_all_item_name);
            tv_type= (TextView) itemView.findViewById(R.id.id_search_all_item_type);
            tv_time= (TextView) itemView.findViewById(R.id.id_search_all_item_time);
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
