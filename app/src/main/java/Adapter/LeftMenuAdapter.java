package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xx.htmlproject.R;

import java.util.ArrayList;
import java.util.HashMap;

import cn.bmob.v3.BmobUser;

/**
 * Created by XX on 2016/5/16.
 */
public class LeftMenuAdapter extends   RecyclerView.Adapter<LeftMenuAdapter.MyViewHolder>{
    private ArrayList<HashMap<String,String>> stringList;
    private int[] images;
    private LayoutInflater layoutInflater;
    private Context context;

    public interface RecyItemOnclick
    {
        void onItemClick(View view,int postion);
    }

    private RecyItemOnclick recyItemOnclick;

    public void setRecyItemOnclick(RecyItemOnclick Listener){
        this.recyItemOnclick=Listener;
    }

    public LeftMenuAdapter(Context context, ArrayList<HashMap<String,String>> stringList, int[] images) {
        this.context=context;
        this.stringList = stringList;
        this.images = images;
        this.layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= layoutInflater.inflate(R.layout.left_menu_item_layout,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view,recyItemOnclick);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.textView.setText(stringList.get(position).get("title"));
        holder.textView.setTextColor(Color.parseColor(stringList.get(position).get("color")));
        if (BmobUser.getCurrentUser(context)==null) {
            images= new int[]{R.drawable.home, R.drawable.pim, R.drawable.about, R.drawable.exit};
        }
        holder.imageView.setImageResource(images[position]);
    }

    public void haveCurrentUser()
    {
        stringList.clear();
        //有用户对象

        HashMap map=new HashMap();
        map.put("title","主页");
        map.put("color","#A2C232");
        stringList.add(map);

        HashMap map2=new HashMap();
        map2.put("title","信息");
        map2.put("color","#23C7BE");
        stringList.add(map2);

        HashMap map3=new HashMap();
        map3.put("title","关于");
        map3.put("color","#9A95F9");
        stringList.add(map3);

        HashMap map4=new HashMap();
        map4.put("title","注销");
        map4.put("color","#FDA101");
        stringList.add(map4);

        HashMap map5=new HashMap();
        map5.put("title","退出");
        map5.put("color","#F26C60");
        stringList.add(map5);

        notifyDataSetChanged();
    }

    public void noCurrentUser()
    {
        stringList.clear();
        //无用户对象
        HashMap map=new HashMap();
        map.put("title","主页");
        map.put("color","#A2C232");
        stringList.add(map);

        HashMap map2=new HashMap();
        map2.put("title","登录");
        map2.put("color","#23C7BE");
        stringList.add(map2);

        HashMap map3=new HashMap();
        map3.put("title","关于");
        map3.put("color","#9A95F9");
        stringList.add(map3);

        HashMap map4=new HashMap();
        map4.put("title","退出");
        map4.put("color","#F26C60");
        stringList.add(map4);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imageView;
        private TextView textView;
        private RecyItemOnclick recyItemOnclick;
        public MyViewHolder(View itemView,RecyItemOnclick recyItemOnclick) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.id_left_menu_item_imageview);
            textView= (TextView) itemView.findViewById(R.id.id_left_menu_item_textview);
            this.recyItemOnclick=recyItemOnclick;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recyItemOnclick!=null)
            {
                int positon=getLayoutPosition();
                recyItemOnclick.onItemClick(v,positon);
            }
        }
    }
}
