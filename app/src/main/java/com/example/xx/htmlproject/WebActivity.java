package com.example.xx.htmlproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.MyFragmentPagerAdapter;
import Adapter.SelectAnimDialogAdapter;
import App.MyActivityStackManager;
import Fragment.WebFragment;
import HtmlAnim.AccordionTransformer;
import HtmlAnim.BackgroundToForegroundTransformer;
import HtmlAnim.CubeInTransformer;
import HtmlAnim.CubeOutTransformer;
import HtmlAnim.DefaultTransformer;
import HtmlAnim.ForegroundToBackgroundTransformer;
import HtmlAnim.RotateDownTransformer;
import HtmlAnim.RotateUpTransformer;
import HtmlAnim.ScaleInOutTransformer;
import HtmlAnim.StackTransformer;
import HtmlAnim.TabletTransformer;
import HtmlAnim.ZoomOutSlideTransformer;
import HtmlAnim.ZoomOutTranformer;
import StartActivityAnim.DepthPageTransformer;
import StartActivityAnim.ZoomOutPageTransformer;
import utils.ViewPagerScroller;

/**
 *展示网页新闻
 *
 */
public class WebActivity extends AppCompatActivity {

    private MyViewPager viewPager;//自定义轮播控件
    private List<Fragment> list;
    /**
     * 屏幕尺寸
     */
    private int mDensity;
    /**
     * 下标
     */
    private  int Index = 0;

    private List<String> URLList=new ArrayList<>();//HTML  URL地址集合
    private TextView title;
    private int POSITION;//判断点击的哪一页
    private Toolbar toolbar;

    private String NEWSPAPER_ID;//用于判断评论所属报纸

    private ImageView iv_cancel;

    @Override
    protected void onCreate(Bundle  icicle) {
//        super.onCreate(savedInstanceState);

        super.onCreate(icicle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //屏幕保持高亮
        setContentView(R.layout.webactivity);

        /**把当前activity加入管理队列中*/
        MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
        myActivityStackManager.addActivity(WebActivity.this);

        initToolBar();
        initView();

    }



    private void initView() {
        /**
         * activity字符串是其他activity用intent跳转到当前activity时传的一个特殊值，用于判断是从哪个activity跳转的
         * *
         */
        String activity=getIntent().getExtras().get("activity").toString();

        if (activity.equals("NewsPagesActivity"))
        {
            URLList = (List<String>) getIntent().getExtras().get("HTML_URL_LIST");
            POSITION= (int) getIntent().getExtras().get("POSITION");
            NEWSPAPER_ID=getIntent().getExtras().getString("SHORTNAME")+getIntent().getExtras().getString("PAPERDATE");

        }
        else if (activity.equals("SearchNewsPaperResultActivity")){
            URLList = (List<String>) getIntent().getExtras().get("HTML_URL_LIST");
            NEWSPAPER_ID=getIntent().getExtras().getString("SHORTNAME")+getIntent().getExtras().getString("PAPERDATE");
        }

        mDensity = getDensity();
        //绑定
        viewPager = (MyViewPager) findViewById(R.id.id_viewpager);
        /**
         * 通过Fragment作为ViewPager的数据源
         */
        list = new ArrayList<Fragment>();

        for(int i=0;i<URLList.size();i++) {
            list.add(WebFragment.newInstance(URLList.get(i), mDensity));
        }



        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setOffscreenPageLimit(2);//缓存个数
        viewPager.setAdapter(fAdapter);


        ViewPagerScroller viewPagerScroller=new ViewPagerScroller(WebActivity.this);
        viewPagerScroller.initViewPagerScroll(viewPager);

        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());//设置一个默认的切换动画

        if (activity.equals("NewsPagesActivity"))
        {
            viewPager.setCurrentItem(POSITION,true);
            title.setText("第 "+(POSITION+1)+" 版");
        }
//
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                title.setText("第 "+(position+1)+" 版");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initToolBar() {
        toolbar= (Toolbar) findViewById(R.id.id_webactivity_toolbar);
        title= (TextView) findViewById(R.id.id_web_title);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_comment)
        {
            Intent intent=new Intent(WebActivity.this,CommentActivity.class);
            intent.putExtra("NEWSPAPER_ID",NEWSPAPER_ID);//报纸标识
            startActivity(intent);
        }else if (id==R.id.action_anim)
        {
            final AlertDialog alert=new AlertDialog.Builder(this).create();
            alert.show();
            Window window=alert.getWindow();
            window.setContentView(R.layout.demand_popup_select);
            window.setWindowAnimations(R.style.dialog_anim);
            iv_cancel= (ImageView) window.findViewById(R.id.iv_cancel);
            iv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            RecyclerView recyclerView= (RecyclerView) window.findViewById(R.id.recyclerView);
            final List<String> animNameList=new ArrayList<String>();
            animNameList.add("平移");
            animNameList.add("折叠平移");
            animNameList.add("背景缩小");
            animNameList.add("内立方体");
            animNameList.add("立方体");
            animNameList.add("背景上升");
            animNameList.add("背景放大");
            animNameList.add("向下旋转");
            animNameList.add("向下旋转");
            animNameList.add("比例缩放");
            animNameList.add("堆叠");
            animNameList.add("板面切换");
            animNameList.add("缩放滑动");
            animNameList.add("渐变");


            recyclerView.setLayoutManager(new LinearLayoutManager(WebActivity.this,LinearLayoutManager.VERTICAL,false));

            SelectAnimDialogAdapter selectAnimDialogAdapter=new SelectAnimDialogAdapter(WebActivity.this, (ArrayList<String>) animNameList);

            selectAnimDialogAdapter.setRecyItemOnclick(new SelectAnimDialogAdapter.RecyItemOnclick() {
                @Override
                public void onItemClick(View view, int postion) {

                    switch (postion)
                    {

                        case 0:
                            viewPager.setPageTransformer(true,new DefaultTransformer());
                            break;
                        case 1:
                            viewPager.setPageTransformer(true,new AccordionTransformer());
                            break;
                        case 2:
                            viewPager.setPageTransformer(true,new BackgroundToForegroundTransformer());
                            break;
                        case 3:
                            viewPager.setPageTransformer(true,new CubeInTransformer());
                            break;
                        case 4:
                            viewPager.setPageTransformer(true,new CubeOutTransformer());
                            break;
                        case 5:
                            viewPager. setPageTransformer(true,new DepthPageTransformer());
                            break;
                        case 6:
                            viewPager. setPageTransformer(true,new ForegroundToBackgroundTransformer());
                            break;

                        case 7:
                            viewPager.setPageTransformer(true,new RotateDownTransformer());
                            break;
                        case 8:
                            viewPager.setPageTransformer(true,new RotateUpTransformer());
                            break;
                        case 9:
                            viewPager. setPageTransformer(true,new ScaleInOutTransformer());
                            break;
                        case 10:
                            viewPager.setPageTransformer(true,new StackTransformer());
                            break;
                        case 11:
                            viewPager.setPageTransformer(true,new TabletTransformer());
                            break;
                        case 12:
                            viewPager. setPageTransformer(true,new ZoomOutSlideTransformer());
                            break;
                        case 13:
                            viewPager. setPageTransformer(true,new ZoomOutPageTransformer());
                            break;
                        case 14:
                            viewPager.setPageTransformer(true,new ZoomOutTranformer());
                            break;
                    }
                    Toast.makeText(WebActivity.this, animNameList.get(postion), Toast.LENGTH_LONG).show();
                    alert.dismiss();
                }
            });
            recyclerView.setAdapter(selectAnimDialogAdapter);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }


    /**
     * 获取屏幕尺寸
     * @return
     */
    private int getDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }



    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setLifeCycle(MyViewPager.RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPager.setLifeCycle(MyViewPager.PAUSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager.setLifeCycle(MyViewPager.DESTROY);
    }

}
