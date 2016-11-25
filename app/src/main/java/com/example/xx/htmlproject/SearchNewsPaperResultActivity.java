package com.example.xx.htmlproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import Adapter.SearchNewsPaperResultAdapter;
import App.HtmlURL;
import App.MyActivityStackManager;
import App.MyApplication;
import GsonBean.SearchData;
import GsonBean.SearchPages;
import RecycleViewAnimUtils.ScaleInAnimatorAdapter;
import utils.FilterHtml;

/**
 * Created by XX on 2016/5/6.
 */
public class SearchNewsPaperResultActivity extends AppCompatActivity implements SearchNewsPaperResultAdapter.RecyItemOnclick{
    private String name;
    private String searchURL;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private SearchNewsPaperResultAdapter searchNewsPaperResultAdapter;

    private ArrayList<SearchData> listData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_newspaper_result_layout);
        /**把当前activity加入管理队列中*/
        MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
        myActivityStackManager.addActivity(SearchNewsPaperResultActivity.this);


        String activity = getIntent().getExtras().getString("activity");
        if (activity.equals("SearchActivity"))
        {
            searchURL=getIntent().getExtras().getString("SEARCH_URL");
        }else if (activity.equals("SearchAllNewsPaperActivity")){
            name=getIntent().getExtras().getString("NAME");
            if (name!=null) {
                if (!name.isEmpty()) {
                    searchURL = HtmlURL.SEARCH_NEWS_PAPER_URL + "?" + "paperoffice=" + name;
                }
            }
        }else if(activity.equals("MainActivity"))
        {
            name=getIntent().getExtras().getString("NAME");
            if (name!=null) {
                if (!name.isEmpty()) {
                    searchURL = HtmlURL.SEARCH_NEWS_PAPER_URL + "?" + "paperoffice=" + name;
                }
            }
        }


        initView();
        initToolbar();
        getData();
    }

    private void initView() {
        recyclerView= (RecyclerView) findViewById(R.id.id_search_result_recycleView);
    }

    private void initToolbar() {
        toolbar= (Toolbar) findViewById(R.id.id_toolbar);
        if (toolbar!=null) {
            toolbar.setTitle("返回");
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
    }
    private void getData() {
        requestQueue= MyApplication.getRequestQueue();

        StringRequest stringRequest= new StringRequest(Request.Method.GET, searchURL, new Response.Listener<String>() {//通过传过来的name查询报纸
            @Override
            public void onResponse(String s) {
                String filterStirng= FilterHtml.filterHtml(s);//过滤HTML字符
                Gson gson=new Gson();
                SearchPages searchPages=gson.fromJson(filterStirng,SearchPages.class);

                listData=searchPages.getData();
                initAdapter(searchPages);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        stringRequest.setTag("SearchNewsPaperResultActivity");
        requestQueue.add(stringRequest);
    }

    private void initAdapter(SearchPages searchPages) {
        if (searchPages.getData().size()==0)
        {
            Snackbar.make(recyclerView,"抱歉，查询不到报纸!",Snackbar.LENGTH_SHORT).show();
        }
        searchNewsPaperResultAdapter=new SearchNewsPaperResultAdapter(this,searchPages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ScaleInAnimatorAdapter scaleInAnimatorAdapter=new ScaleInAnimatorAdapter(searchNewsPaperResultAdapter,recyclerView);//设置adapter动画
        scaleInAnimatorAdapter.getViewAnimator().setInitialDelayMillis(500);//延长动画时间

        searchNewsPaperResultAdapter.setRecyItemOnclick(this);//设置点击事件
        recyclerView.setAdapter(scaleInAnimatorAdapter);//为recyclerview设置适配器
    }



    @Override
    public void onItemClick(View view, int postion) {
      /**跳转到NewsPagesActivity*/

            Intent intent=new Intent(SearchNewsPaperResultActivity.this,NewsPagesActivity.class);
             intent.putExtra("NAME",listData.get(postion).getName());
             intent.putExtra("DATE",listData.get(postion).getDate());
            intent.putExtra("activity","SearchNewsPaperResultActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);//进入activity动画

//        Toast.makeText(getApplicationContext(),"时间"+time,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        requestQueue.cancelAll("SearchNewsPaperResultActivity");
    }
}
