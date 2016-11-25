package com.example.xx.htmlproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import Adapter.CommentAdapter;
import App.MyActivityStackManager;
import Data.Comment;
import Data.MyUser;
import RecycleViewAnimUtils.ScaleInAnimatorAdapter;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import utils.HideKeyBoard;

/**
 * Created by haiyuan 1995 on 2016/5/21.
 */

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText content;
    private TextView send;

    private String NEWSPAPER_ID;

    private AlertDialog alert;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);

        MyActivityStackManager.getInstance().addActivity(CommentActivity.this);//增加到activity管理
        NEWSPAPER_ID=getIntent().getExtras().getString("NEWSPAPER_ID");
//        Bmob.initialize(CommentActivity.this,"62fa233d7333debb873e0e38bd4ec4ce");//测试完成删除

        initToolbar();
        initView();
        initData();




    }

    private void initData() {

        alert=new AlertDialog.Builder(CommentActivity.this).create();
        alert.show();
        Window window=alert.getWindow();
        window.setContentView(R.layout.comment_dialog_layout);
        TextView textView= (TextView) window.findViewById(R.id.id_comment_dialog_text);
        textView.setText("正在获取评论...");

        BmobQuery<Comment> query=new BmobQuery<Comment>();
        query.addWhereEqualTo("newspaperId",NEWSPAPER_ID);
        query.order("-updatedAt");
        query.include("user");
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                System.out.println("获取评论成功!");

                if (list.isEmpty())
                {

                    Snackbar.make(recyclerView,"没有评论哦!",Snackbar.LENGTH_LONG).show();
                    alert.dismiss();
                }
                else {
                    initAdapter(list);
                }
            }

            @Override
            public void onError(int i, String s) {
                System.out.println("获取评论失败!"+s);
                alert.dismiss();
            }
        });


    }

    private void initAdapter(List<Comment> list) {

        recyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this,LinearLayoutManager.VERTICAL,false));
        CommentAdapter commentAdapter=new CommentAdapter(CommentActivity.this,list);//传递评论信息和用户信息
        ScaleInAnimatorAdapter scaleInAnimatorAdapter=new ScaleInAnimatorAdapter(commentAdapter,recyclerView);
        recyclerView.setAdapter(scaleInAnimatorAdapter);
        alert.dismiss();
    }

    private void initView() {
        recyclerView= (RecyclerView) findViewById(R.id.id_comment_recyclerview);
        content= (EditText) findViewById(R.id.id_comment_edittext);
        send= (TextView) findViewById(R.id.id_comment_send);
        send.setClickable(false);

        /**文本内容变更监听
         * */
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0)
                {
                    send.setTextColor(Color.parseColor("#1ba1e2"));
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Toast.makeText(CommentActivity.this,"send",Toast.LENGTH_LONG).show();
                            if (BmobUser.getCurrentUser(CommentActivity.this)==null)
                            {
//                                //用户没有登录，跳转到登录界面
//                                Intent intent=new Intent(CommentActivity.this,LoginActivity.class);
//                                intent.putExtra("activity","CommentActivity");
//                                startActivity(intent);
                                Toast.makeText(CommentActivity.this,"请先到首页进行登录!",Toast.LENGTH_LONG).show();
                            }else {

                                alert=new AlertDialog.Builder(CommentActivity.this).create();
                                alert.show();
                                Window window=alert.getWindow();
                                window.setContentView(R.layout.comment_dialog_layout);

                                Comment comment = new Comment();


                                comment.setNewspaperId(NEWSPAPER_ID);
                                comment.setContent(content.getText().toString());
                                MyUser user = BmobUser.getCurrentUser(CommentActivity.this, MyUser.class);
                                comment.setUser(user);

                                comment.save(CommentActivity.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        HideKeyBoard.hide(CommentActivity.this);//隐藏键盘
                                        content.setText("");
                                        content.clearFocus();
                                        alert.dismiss();
                                        Toast.makeText(CommentActivity.this,"评论成功!",Toast.LENGTH_LONG).show();

                                        refreshData();//刷新评论列表
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(CommentActivity.this,"评论失败!",Toast.LENGTH_LONG).show();
                                        alert.dismiss();
                                    }
                                });
                            }

                        }
                    });

                }else if (s.length()==0)
                {
                    send.setTextColor(Color.parseColor("#FFAAAAAA"));
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //void
                        }
                    });
                }
            }
        });




    }



    private void refreshData() {

        BmobQuery<Comment> query=new BmobQuery<Comment>();
        query.addWhereEqualTo("newspaperId",NEWSPAPER_ID);
        query.order("-updatedAt");
        query.include("user");
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                System.out.println("获取评论成功!");

                if (list.isEmpty())
                {
                    Snackbar.make(recyclerView,"没有评论哦!",Snackbar.LENGTH_LONG).show();
                }
                else {
                    initAdapter(list);
                }
            }
            @Override
            public void onError(int i, String s) {
                System.out.println("获取评论失败!"+s);
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.id_comment_toolbar);
        toolbar.setTitle("评论");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();//返回键结束当前页面
            }
        });
    }

}
