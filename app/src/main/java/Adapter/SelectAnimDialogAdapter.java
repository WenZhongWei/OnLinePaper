package Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xx.htmlproject.R;

import java.util.ArrayList;

/**
 * Created by LanHaiYuan.
 */
public class SelectAnimDialogAdapter extends RecyclerView.Adapter<SelectAnimDialogAdapter.MyViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> animNameList;

    public interface RecyItemOnclick
    {
        void onItemClick(View view,int postion);
    }
    private  RecyItemOnclick recyitemonclick;

    public void setRecyItemOnclick(RecyItemOnclick Listener){
        this.recyitemonclick=Listener;
    }

    public SelectAnimDialogAdapter(Context mContext ,ArrayList<String> animNameList) {
        this.mContext = mContext;
        this.animNameList=animNameList;
        mLayoutInflater=LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=mLayoutInflater.inflate(R.layout.select_anim_dialog_item_layout,parent,false);//测试用card item layout
        MyViewHolder myViewHolder=new MyViewHolder(view,recyitemonclick);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tv_num.setText(String.valueOf(position+1));
        holder.tv_animName.setText(animNameList.get(position));

    }


    @Override
    public int getItemCount() {
        return animNameList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_num;
        TextView tv_animName;
        RecyItemOnclick recyItemOnclick;
        public MyViewHolder(View itemView,RecyItemOnclick recyItemOnclick) {
            super(itemView);
            tv_num= (TextView) itemView.findViewById(R.id.tv_num);
            tv_animName= (TextView) itemView.findViewById(R.id.tv_anim);

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
