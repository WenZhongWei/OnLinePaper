package Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xx.htmlproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import GsonBean.NewsPaperTypes;

/**
 * Created by LanHaiYuan.
 * 2016/04/15
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private NewsPaperTypes newsPaperTypes;

    public interface RecyItemOnclick
    {
        void onItemClick(View view,int postion);
    }
    private  RecyItemOnclick recyitemonclick;

    public void setRecyItemOnclick(RecyItemOnclick Listener){
        this.recyitemonclick=Listener;
    }

    public MainAdapter(Context mContext , NewsPaperTypes newsPaperTypes) {
        this.mContext = mContext;
        this.newsPaperTypes=newsPaperTypes;
        mLayoutInflater=LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=mLayoutInflater.inflate(R.layout.main_item_layout,parent,false);//测试用card item layout
        MyViewHolder myViewHolder=new MyViewHolder(view,recyitemonclick);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_title.setTypeface(Typeface.MONOSPACE);

        holder.tv_title.setText(newsPaperTypes.getData().get(position).getPresstype());

        Date currentTime =new Date();
        SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd");
        String time=timeFormat.format(currentTime);
        holder.tv_time.setText(time);

        Glide.with(mContext).load(newsPaperTypes.getData().get(position).getLogoaddress()).animate(R.anim.fade).into(holder.iv_cardview_bg);

    }


    @Override
    public int getItemCount() {
        return 5;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_title;
        ImageView iv_cardview_bg;
        TextView tv_time;
        RecyItemOnclick recyItemOnclick;
        public MyViewHolder(View itemView,RecyItemOnclick recyItemOnclick) {
            super(itemView);
            tv_title= (TextView) itemView.findViewById(R.id.id_tv_title);
            iv_cardview_bg= (ImageView) itemView.findViewById(R.id.id_cardview_image);
            tv_time= (TextView) itemView.findViewById(R.id.id_main_time);
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
