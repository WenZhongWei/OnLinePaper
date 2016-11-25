package com.example.xx.htmlproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import Adapter.NewsAdapter;
import App.HtmlURL;
import App.MyActivityStackManager;
import App.MyApplication;
import GsonBean.NewsPaper;
import utils.FilterHtml;

/**
 * Created by XX on 2016/4/15.
 */
public class NewsActivity extends AppCompatActivity implements NewsAdapter.RecyItemOnclick{
    private List<String> mNews;
    private NewsAdapter mNewsAdapter;
    private RecyclerView mRecyclerView;
    private TextView toolbar_title;
    private List<String> LogoURL;
    private FloatingActionButton fab;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_layout);
        /**把当前activity加入管理队列中*/
        MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
        myActivityStackManager.addActivity(NewsActivity.this);

        type = getIntent().getExtras().getString("TYPE_DATA");
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initToolbar();
        initView();
        VolleyGetNewsPaper();

    }

    private void initAdapter() {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        mNewsAdapter=new NewsAdapter(this,mNews,LogoURL);
        mNewsAdapter.setRecyItemOnclick(NewsActivity.this);//为适配器设置点击监听
        mRecyclerView.setAdapter(mNewsAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//为RecyclerView设置增加删除动画
    }

    private void initToolbar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.id_toolbar);
       toolbar_title= (TextView) findViewById(R.id.toolbar_title);
        toolbar.setTitle(" ");

        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitle("选择报纸");
        //通过CollapsingToolbarLayout修改字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后Toolbar上字体的颜色
    }
    private void VolleyGetNewsPaper() {//得到全部报纸
        StringRequest stringRequest= new StringRequest(Request.Method.GET, HtmlURL.All_NEWS_PAPER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String filterStirng= FilterHtml.filterHtml(s);//过滤HTML字符
                Gson gson=new Gson();
                NewsPaper newsPaper=gson.fromJson(filterStirng,NewsPaper.class);

                //初始化报纸名数据
                initData(newsPaper);
                //初始化适配器
                initAdapter();

                System.out.println(newsPaper.getData().get(0).getName());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        stringRequest.setTag("NewsActivity");
        MyApplication.getRequestQueue().add(stringRequest);
    }

    private void initData(NewsPaper newsPaper) {

         LogoURL = new ArrayList<String>();

        for (int i=0;i<newsPaper.getData().size();i++) {
            if (newsPaper.getData().get(i).getType().equals(type)) {
                LogoURL.add(newsPaper.getData().get(i).getLogoaddress());
            }
        }


        mNews=new ArrayList<String>();
        for(int i=0;i<newsPaper.getData().size();i++)
        {
            if (newsPaper.getData().get(i).getType().equals(type))
            {
                mNews.add((newsPaper.getData().get(i).getName()));
            }
        }

    }

    private void initView()
    {
        mRecyclerView= (RecyclerView) findViewById(R.id.id_recycleView);
        fab= (FloatingActionButton) findViewById(R.id.news_layout_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int postion) {
//        Toast.makeText(NewsActivity.this,""+postion,Toast.LENGTH_LONG).show();
        Intent intent=new Intent(NewsActivity.this, NewsPagesActivity.class);
        intent.putExtra("activity","NewsActivity");
        intent.putExtra("NAME",mNews.get(postion));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int postion) {
//        Toast.makeText(NewsActivity.this,"LONG"+postion,Toast.LENGTH_LONG).show();
        
    }
    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getRequestQueue().cancelAll("NewsActivity");
    }
}
