package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.xx.htmlproject.R;

import java.util.List;

import Data.Comment;

/**
 * Created by haiyuan 1995 on 2016/5/21.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private List<Comment> list;
    private Context context;
    private LayoutInflater layoutInflater;

    public interface RecyItemOnclick
    {
        void onItemClick(View view,int postion);
    }

    private RecyItemOnclick recyItemOnclick;

    public void setRecyItemOnclick(RecyItemOnclick Listener){
        this.recyItemOnclick=Listener;
    }

    public CommentAdapter(Context context,List<Comment> list) {
        this.list = list;
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.comment_item_layout,parent,false);

        return new MyViewHolder(view,recyItemOnclick);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.nick.setText(list.get(position).getUser().getUsername());
        holder.content.setText(list.get(position).getContent());

        String imageurl=list.get(position).getUser().getIconUrl();
        if (imageurl == null || imageurl.isEmpty()) {

            Glide.with(context).load(R.drawable.mine_mess).asBitmap().centerCrop()
                    .into(setRoundedImage(holder.imageView));
            holder.imageView.setTag(R.id.image_tag, R.drawable.mine_mess);
        } else {

            Glide.with(context).load(imageurl).asBitmap().centerCrop().into(setRoundedImage(holder.imageView));
            holder.imageView.setTag(R.id.image_tag, imageurl);//以url为tag标记

        }
        String sex=list.get(position).getUser().getSex();
        if (sex!=null){
            if (sex.equals("男")){
                holder.sex.setImageResource(R.mipmap.man);
            }else if (sex.equals("女")){
                holder.sex.setImageResource(R.mipmap.woman);
            }
        }
        holder.time.setText(list.get(position).getCreatedAt());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imageView;
        private TextView nick;
        private TextView time;
        private TextView content;
        private ImageView sex;
        private RecyItemOnclick recyItemOnclick;

        public MyViewHolder(View itemView,RecyItemOnclick recyItemOnclick) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.id_comment_item_imageview);
            nick= (TextView) itemView.findViewById(R.id.id_comment_item_nick);
            time= (TextView) itemView.findViewById(R.id.id_comment_item_time);
            content= (TextView) itemView.findViewById(R.id.id_comment_item_content);
            sex= (ImageView) itemView.findViewById(R.id.id_comment_item_sex);
            this.recyItemOnclick= recyItemOnclick;
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


    private BitmapImageViewTarget setRoundedImage(final ImageView imageView)
    {
        BitmapImageViewTarget bitmapImageViewTarget=new BitmapImageViewTarget(imageView)
        {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                roundedBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(roundedBitmapDrawable);
            }
        };
        return bitmapImageViewTarget;
    }
}
