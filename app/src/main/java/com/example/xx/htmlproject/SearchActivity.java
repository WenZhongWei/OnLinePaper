package com.example.xx.htmlproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.angmarch.views.NiceSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Adapter.SearchAdapter;
import App.HtmlURL;
import App.MyActivityStackManager;
import App.MyApplication;
import GsonBean.NewsPaper;
import GsonBean.NewsPaperTypes;
import utils.FilterHtml;

/**
 * Created by XX on 2016/5/4.
 */
public class SearchActivity extends AppCompatActivity implements SearchAdapter.RecyItemOnclick{
    private Toolbar toolbar;
    private NiceSpinner spinner_type,spinner_baokan;
    //    private ArrayList<TypeData> type_list;
    private CalendarView calendarView;
    private String TYPE="";//spinner选择的报刊类型
    private String NAME="";//spinner选择的报刊名字
    private String DATE="";//calendarView选择的报刊日期
    private RequestQueue requestQueue;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        /**把当前activity加入管理队列中*/
        MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
        myActivityStackManager.addActivity(SearchActivity.this);

        initView();

        initToolBar();
        initData();
        initCalendarView();

    }

    private void initView() {
        spinner_type= (NiceSpinner) findViewById(R.id.id_search_spinner_type);
        spinner_baokan= (NiceSpinner) findViewById(R.id.id_search_spinner_baokan);

        SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd");//吧date数据转化为特定格式日期
        String defaultDate=timeFormat.format(new Date());//默认为当前日期
        DATE=defaultDate;


        fab= (FloatingActionButton) findViewById(R.id.id_search_fab);//浮动按钮
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"搜索中...",Snackbar.LENGTH_SHORT).setAction("隐藏", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //留空，不用调用dismiss ,调用了反而布局有问题
                    }
                }).show();

                if (!TYPE.isEmpty() && !NAME.isEmpty() && !DATE.isEmpty()) {
                    final String searchURL = HtmlURL.SEARCH_NEWS_PAPER_URL + "?" + "paperoffice=" + NAME + "&" + "date=" + DATE;
                    //查询报刊请求
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //void
                            Intent intent = new Intent(SearchActivity.this, SearchNewsPaperResultActivity.class);
                            intent.putExtra("SEARCH_URL", searchURL);
                            intent.putExtra("activity", "SearchActivity");
                            startActivity(intent);
                        }
                    },1000);

                }
                else
                {
//                        Toast.makeText(SearchActivity.this,"条件不足无法搜索！",Toast.LENGTH_LONG).show();
                    Snackbar.make(view,"条件不足无法搜索!",Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                            .show();
                }
            }
        });
    }


    private void initCalendarView() {
        calendarView= (CalendarView) findViewById(R.id.id_search_calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                String dateString = year + "-" + (1+month) + "-" + dayOfMonth ;//月份为0-11

                SimpleDateFormat stringForTime=new SimpleDateFormat("yyyy-MM-dd");//吧数据源中的日期字符串转换为Date数据
                Date date=null;
                try {
                    date=stringForTime.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd");//吧date数据转化为特定格式日期
                String time=timeFormat.format(date);
                DATE=time;
                Toast.makeText(getApplicationContext(), time, Toast.LENGTH_LONG).show();
            }
        });

    }



    private void initData() {

        getNewsPaperType();

    }







    private void getNewsPaperType() {
        MyApplication app= (MyApplication) getApplication();
        requestQueue=app.getRequestQueue();

        StringRequest stringRequest= new StringRequest(Request.Method.GET, HtmlURL.NEWS_PAPER_TYPES, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String filterStirng= FilterHtml.filterHtml(s);//过滤字符

                Gson gson=new Gson();
                NewsPaperTypes newsPaperTypes=gson.fromJson(filterStirng, NewsPaperTypes.class);
                initSpinnerType(newsPaperTypes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        stringRequest.setTag("SearchActivity");
        requestQueue.add(stringRequest);
    }

    private void VolleyGetCurrentTypePapers() {//得到改类型下的全部报纸
        StringRequest stringRequest= new StringRequest(Request.Method.GET, HtmlURL.All_NEWS_PAPER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String filterStirng= FilterHtml.filterHtml(s);//过滤HTML字符
                Gson gson=new Gson();
                NewsPaper newsPaper=gson.fromJson(filterStirng,NewsPaper.class);

                //初始化报纸名数据
                initSpinnerBaokan(newsPaper);

                System.out.println(newsPaper.getData().get(0).getName());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        stringRequest.setTag("SearchActivity");
        MyApplication.getRequestQueue().add(stringRequest);
    }

    private void initSpinnerBaokan(NewsPaper newsPaper) {
        List<String> listBaokan = new ArrayList<>();//获取该类型下的报刊
        boolean flag=false;//用于判断该类型下是否没有报刊，默认没有
        for (int i = 0; i < newsPaper.getData().size(); i++) {
            if (newsPaper.getData().get(i).getType().equals(TYPE)) {
                listBaokan.add((newsPaper.getData().get(i).getName()));
                flag=true;
            }
        }
        if (flag==false)
        {
            listBaokan.add("该类型暂无报刊...");
        }


        ArrayAdapter baokan_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listBaokan);
        baokan_adapter.setDropDownViewResource(R.layout.spinner_list_item);
        spinner_baokan.setTintColor(R.color.colorPrimaryDark);
        spinner_baokan.setAdapter(baokan_adapter);
        NAME=String.valueOf(baokan_adapter.getItem(0));//默认值

        spinner_baokan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
//                Toast.makeText(SearchActivity.this,"报刊"+adapterView.getItemAtPosition(Integer.parseInt(""+id)),Toast.LENGTH_LONG).show();
                NAME=adapterView.getItemAtPosition(Integer.parseInt(""+id)).toString();//报纸名称
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void initSpinnerType(NewsPaperTypes newsPaperTypes) {

        List<String> type_string_list= new ArrayList<>();

        for (int i=0;i<newsPaperTypes.getData().size();i++)//初始化类型数据list
        {
            type_string_list.add(newsPaperTypes.getData().get(i).getPresstype());
        }

        ArrayAdapter type_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,type_string_list);

        type_adapter.setDropDownViewResource(R.layout.spinner_list_item);

        spinner_type.setTintColor(R.color.colorPrimaryDark);//设置下拉菜单的三角图片颜色
        spinner_baokan.setTintColor(R.color.colorPrimaryDark);
        spinner_type.setAdapter(type_adapter);
        TYPE= String.valueOf(type_adapter.getItem(0));

        if (!TYPE.isEmpty())
        {
            VolleyGetCurrentTypePapers();//第一次进入时默认的类型
        }
        //spinner监听器
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

//                Toast.makeText(SearchActivity.this,"类型"+adapterView.getItemAtPosition(Integer.parseInt(""+id)),Toast.LENGTH_LONG).show();//用id当做position，才能选择正确
                TYPE=adapterView.getItemAtPosition(Integer.parseInt(""+id)).toString();//赋类型值
                VolleyGetCurrentTypePapers();//请求查询当前类型下的所有报刊
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    private void initToolBar() {
        toolbar= (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle("搜索");
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

    @Override
    protected void onStop() {
        super.onStop();
        requestQueue.cancelAll("SearchActivity");
    }

    @Override
    public void onItemClick(View view, int postion) {

    }
}
