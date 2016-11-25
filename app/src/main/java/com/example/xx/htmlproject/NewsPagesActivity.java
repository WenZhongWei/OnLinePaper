package com.example.xx.htmlproject;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Adapter.NewsPagesAdapter;
import App.HtmlURL;
import App.MyActivityStackManager;
import App.MyApplication;
import GsonBean.SearchData;
import GsonBean.SearchPages;
import RecycleViewAnimUtils.SlideInRightAnimatorAdapter;

/**
 * Created by XX on 2016/4/18.
 */
public class NewsPagesActivity extends AppCompatActivity implements NewsPagesAdapter.RecyItemOnclick{

    private RecyclerView mRecyclerView;
    private NewsPagesAdapter mNewsPagesAdapter;
    private TextView  toolbar_title;
    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String NAME;
    private String searchURL;
    private String DATE;
    private ArrayList<SearchData> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pages_layout);

        /**把当前activity加入管理队列中*/
        MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
        myActivityStackManager.addActivity(NewsPagesActivity.this);

        MyApplication app= (MyApplication) getApplication();//获取application中的requestQueue
        mRequestQueue=app.getRequestQueue();
        String activity=  getIntent().getExtras().get("activity").toString();
        if (activity.equals("NewsActivity")) {

            NAME = getIntent().getExtras().getString("NAME");
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM/dd");//吧date数据转化为特定格式日期
            DATE = timeFormat.format(new Date());
        }else if (activity.equals("SearchNewsPaperResultActivity"))
        {
            NAME=getIntent().getExtras().getString("NAME");
            DATE=getIntent().getExtras().getString("DATE");

            SimpleDateFormat stringForTime=new SimpleDateFormat("yyyy-MM-dd");//吧数据源中的日期字符串转换为Date数据
            Date date=null;
            try {
                date=stringForTime.parse(DATE);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM/dd");//吧date数据转化为特定格式日期
            String time=timeFormat.format(date);
            DATE=time;

        }

        initView();
        initToolbar();
        initData();



    }

    private void getData() {
        mSwipeRefreshLayout.setRefreshing(true);//设置刷新状态，作用为打开刷新进度条

        searchURL= HtmlURL.SEARCH_NEWS_PAPER_URL + "?"+"paperoffice=" + NAME+"&"+"date="+DATE;
        final StringRequest stringRequest=new StringRequest(Request.Method.GET, searchURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("网络请求数据");
                jiexiJson(response);//解析json数据
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(mRequestQueue.getCache().get(searchURL)!=null)//请求失败的时候读取缓存
                {
                    System.out.println("请求缓存数据");
                    jiexiJson(new String(mRequestQueue.getCache().get(searchURL).data));//返回的字符串是一个response
                }
                else
                {
                    Toast.makeText(NewsPagesActivity.this, "网络异常，而且没有缓存", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        stringRequest.setTag("NewsPagesActivity");//设置tag，用于取消
        mRequestQueue.add(stringRequest);//添加请求到队列中
    }

    private void jiexiJson(String response) {
        Gson gson=new Gson();
        SearchPages searchPages=gson.fromJson(response,SearchPages.class);
        listData=searchPages.getData();
        if (searchPages.getData().size()==0)
        {
            Snackbar.make(mRecyclerView,"报纸暂时未更新!",Snackbar.LENGTH_LONG).show();
        }
        else {
            String shortname = searchPages.getData().get(0).getShortname();
            /**拼接缩略图片地址**/
            List<String> imageUrl = new ArrayList<String>();

            String time = searchPages.getData().get(0).getDate();
            int pages = Integer.parseInt(searchPages.getData().get(0).getPage());//返回页数
            for (int i = 1; i <= pages; i++) {
                imageUrl.add(searchPages.getData().get(0).getServeraddress() + "JPG/"+shortname+"/"+ time + "/" + i + "/" + i + ".jpg");//获取图片的url
            }
            initAdapter(searchPages, (ArrayList<String>) imageUrl);//初始化adapter
        }
        mSwipeRefreshLayout.setRefreshing(false);//设置刷新状态，作用为关闭进度条
    }


    private void initToolbar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.id_toolbar);
        toolbar_title= (TextView) findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbar_title.setText(NAME);
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();//返回键结束当前页面
            }
        });
    }

    private void initAdapter(SearchPages searchPages,ArrayList<String> imagesURL) {
        mNewsPagesAdapter=new NewsPagesAdapter(NewsPagesActivity.this,searchPages,imagesURL,mRequestQueue);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false));//设置单行卡片个数

        SlideInRightAnimatorAdapter slideInRightAnimatorAdapter=new SlideInRightAnimatorAdapter(mNewsPagesAdapter,mRecyclerView);//设置adapter加载数据时动画效果，右边飞入

        slideInRightAnimatorAdapter.getViewAnimator().setInitialDelayMillis(1000);//延长动画时间

        mNewsPagesAdapter.setRecyItemOnclick(this);

        mRecyclerView.setAdapter(slideInRightAnimatorAdapter);

    }

    private void initData() {

        getData();//从网络中获取json数据并解析

    }

    private void initView() {
        mRecyclerView= (RecyclerView) findViewById(R.id.id_recycleView);
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.id_pages_swiperefrshlayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        // 下面几句代码是为了，第一次进入页面的时候显示加载进度条，setrefresh为true才显示
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        getData();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });


    }

    @Override
    public void onItemClick(View view, int position) {
        /***获得报纸的地址
         * @htmlURL 拼接完成后的URL地址
         * @newsPaperString 用于拼接报纸地址的前缀
         * @shortName 报刊名的拼音缩写
         * @URLlist URL地址集合*/


        List<String> URLlist=new ArrayList<String>();
        String htmlURL;
        String newsPaperString=listData.get(0).getServeraddress()+"HTML/";
        String shortName=listData.get(0).getShortname()+"/";


        for (int i=1;i<=Integer.valueOf(listData.get(0).getPage());i++)//获得数据源中的该报刊页数
        {
            htmlURL=newsPaperString+shortName+DATE+"/"+i+"/"+i+".html";//循环拼接报纸URL
            URLlist.add(htmlURL);
        }

        if (!URLlist.isEmpty())
        {
//            Toast.makeText(NewsPagesActivity.this,position+"",Toast.LENGTH_LONG).show();
            Intent intent= new Intent(NewsPagesActivity.this,WebActivity.class);
            intent.putExtra("activity","NewsPagesActivity");//用于判断从哪个activity跳转到新的activity
            intent.putExtra("POSITION",position);//判断点击第几个页面
            intent.putStringArrayListExtra("HTML_URL_LIST", (ArrayList<String>) URLlist);//传递HTML URL集合
            intent.putExtra("SHORTNAME",shortName);
            intent.putExtra("PAPERDATE",DATE);
            startActivity(intent);
            overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);//进入activity动画
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRequestQueue.cancelAll("NewsPagesActivity");
    }
}
