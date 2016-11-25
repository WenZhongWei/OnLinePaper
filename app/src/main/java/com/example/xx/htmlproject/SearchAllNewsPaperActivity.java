package com.example.xx.htmlproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;

import Adapter.SearchAllNewsPaperAdapter;
import App.HtmlURL;
import App.MyActivityStackManager;
import App.MyApplication;
import GsonBean.NewsData;
import GsonBean.NewsPaper;
import utils.FilterHtml;
import RecycleViewAnimUtils.ScaleInAnimatorAdapter;

/**
 * 报刊栏相关代码
 */
public class SearchAllNewsPaperActivity extends AppCompatActivity implements SearchAllNewsPaperAdapter.RecyItemOnclick{
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private SearchAllNewsPaperAdapter searchAllNewsPaperAdapter;
    private RequestQueue requestQueue;

    private ArrayList<NewsData> listData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_all_newspaper_layout);

        /**把当前activity加入管理队列中*/
        MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
        myActivityStackManager.addActivity(SearchAllNewsPaperActivity.this);

        initView();
        initToolbar();
        initData();
    }

    private void initData() {

        getData();
    }

    private void getData() {
        MyApplication app= (MyApplication) getApplication();
        requestQueue=app.getRequestQueue();

        StringRequest stringRequest= new StringRequest(Request.Method.GET, HtmlURL.All_NEWS_PAPER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String filterStirng= FilterHtml.filterHtml(s);//过滤HTML字符
                Gson gson=new Gson();
                NewsPaper newsPaper=gson.fromJson(filterStirng,NewsPaper.class);
                listData=newsPaper.getData();
                initAdapter(newsPaper);
                System.out.println(newsPaper.getData().get(0).getName());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        stringRequest.setTag("SearchAllNewsPaperActivity");
        requestQueue.add(stringRequest);
    }

    private void initAdapter(NewsPaper newsPaper) {
        searchAllNewsPaperAdapter=new SearchAllNewsPaperAdapter(SearchAllNewsPaperActivity.this,newsPaper);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ScaleInAnimatorAdapter scaleInAnimatorAdapter=new ScaleInAnimatorAdapter(searchAllNewsPaperAdapter,recyclerView);//设置adapter动画
        scaleInAnimatorAdapter.getViewAnimator().setInitialDelayMillis(500);//延长动画时间

        searchAllNewsPaperAdapter.setRecyItemOnclick(this);
        recyclerView.setAdapter(scaleInAnimatorAdapter);//为recyclerview设置适配器
    }

    private void initToolbar() {
        toolbar= (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle("报刊");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        recyclerView= (RecyclerView) findViewById(R.id.id_search_all_recycleView);

    }


    @Override
    public void onItemClick(View view, int postion) {//报刊点击事件

        Intent intent=new Intent(SearchAllNewsPaperActivity.this,SearchNewsPaperResultActivity.class);
        intent.putExtra("NAME",listData.get(postion).getName());
        intent.putExtra("activity","SearchAllNewsPaperActivity");
        startActivity(intent);

    }

    @Override
    protected void onRestart() {
        overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        requestQueue.cancelAll("SearchAllNewsPaperActivity");
    }
}
