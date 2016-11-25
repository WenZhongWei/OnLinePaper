package com.example.xx.htmlproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import Adapter.LeftMenuAdapter;
import Adapter.MainAdapter;
import App.HtmlURL;
import App.MyActivityStackManager;
import App.MyApplication;
import GsonBean.NewsPaperTypes;
import GsonBean.TypeData;
import RecycleViewAnimUtils.AlphaAnimatorAdapter;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import utils.FilterHtml;
import utils.ImageCycleView;
import utils.ReboundScrollView;

/**
 * 程序主界面
 */
public class MainActivity extends AppCompatActivity implements MainAdapter.RecyItemOnclick{

    private RecyclerView mRecyclerView;
    private RecyclerView mMenuRecyclerView;
    private ImageView imageView;

    private DrawerLayout mDrawerLayout;

    private MainAdapter mMainAdapter;
    private ReboundScrollView mReboundScrollView;
    private ImageCycleView mImageCycleView;

    private TextView tv_toolbar_title;
    private TextView tv_name;
    private TextView tv_qianming;
    private TextView tv_hour;
    private TextView tv_PMorAM;

    private ArrayList<TypeData> typeDatas;
    private ArrayList<String> listurl ;//图片URL地址集合

    private long EXIT_TIME=0;//点击两个返回键之间的时间

    private LeftMenuAdapter leftMenuAdapter;
    private MaterialRefreshLayout RefreshLayout;

    private  Handler handler;
    private Runnable runnable;

    public static String APPID = "62fa233d7333debb873e0e38bd4ec4ce";//bmob应用ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);//测试用layout，记得换回来
        Bmob.initialize(getApplicationContext(), APPID);//初始化BMOB

        /**把当前activity加入管理队列中*/
        MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
        myActivityStackManager.addActivity(MainActivity.this);


//        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        initView();
        initToolBar();

        VolleyGetBanner();
        VolleyGetNewsPaperTypes();
        initLeftMenu();


        RefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        VolleyGetBanner();
                        VolleyGetNewsPaperTypes();
                        // 结束下拉刷新...
                        RefreshLayout.finishRefresh();
                    }
                },2000);
            }
        });

//

    }

    private void initLeftMenu() {
        mMenuRecyclerView= (RecyclerView) findViewById(R.id.id_left_menu_recyclerview);
        imageView= (ImageView) findViewById(R.id.id_left_menu_imageview);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                /**加载网络中的用户信息*/
                upDateUserInfo();

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        /**点击头像进入个人信息**/
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BmobUser.getCurrentUser(MainActivity.this)==null)
                {
                    Snackbar.make(v,"请您先登录!",Snackbar.LENGTH_LONG).show();
                }
                else{
                    startActivity(new Intent(MainActivity.this,PersonInfoActivity.class));
                }
            }
        });


        int[] images={R.drawable.home,R.drawable.pim,R.drawable.about,R.drawable.cancel,R.drawable.exit};
        ArrayList<HashMap<String,String>> stringList= new ArrayList<>();
        HashMap<String, String> map= new HashMap<>();
        map.put("title","主页");
        map.put("color","#A2C232");
        stringList.add(map);

        HashMap<String, String> map2= new HashMap<>();
        map2.put("title","登录");
        map2.put("color","#23C7BE");
        stringList.add(map2);

        HashMap<String, String> map3= new HashMap<>();
        map3.put("title","关于");
        map3.put("color","#9A95F9");
        stringList.add(map3);

        HashMap<String, String> map4= new HashMap<>();
        map4.put("title","退出");
        map4.put("color","#F26C60");
        stringList.add(map4);


        leftMenuAdapter=new LeftMenuAdapter(MainActivity.this,stringList,images);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
        mMenuRecyclerView.setLayoutManager(linearLayoutManager);


        leftMenuAdapter.setRecyItemOnclick(new LeftMenuAdapter.RecyItemOnclick() {
            @Override
            public void onItemClick(View view, int postion) {
                if (BmobUser.getCurrentUser(MainActivity.this)==null) {//登录前菜单
                    switch (postion) {
                        case 0://主页
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                    Toast.makeText(MainActivity.this,"主页",Toast.LENGTH_LONG).show();
                                }
                            },300);
                            break;
                        case 1://登录
                            startActivity(new Intent(MainActivity.this, LoginActivity.class).putExtra("activity","MainActivity"));
//                            finish();
                            break;
                        case 2://关于
                            startActivity(new Intent(MainActivity.this,AboutActivity.class));
                            break;
                        case 3://退出
                            Snackbar.make(view, "您确定要退出？", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //退出程序
                                    MyActivityStackManager.getInstance().ExitApplication(MainActivity.this);
                                }
                            }).show();
                            break;
                    }
                }
                else//登录之后的菜单
                {
                    switch (postion) {
                        case 0://主页
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                    Toast.makeText(MainActivity.this,"主页",Toast.LENGTH_LONG).show();
                                }
                            },300);
                            break;
                        case 1://个人信息
                            startActivity(new Intent(MainActivity.this, PersonInfoActivity.class));
                            break;
                        case 2://关于
                            startActivity(new Intent(MainActivity.this,AboutActivity.class));
                            break;
                        case 3://注销
                            zhuxiao(view);
                            break;
                        case 4:
                            Snackbar.make(view, "您确定要退出？", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //退出程序
                                    MyActivityStackManager.getInstance().ExitApplication(MainActivity.this);
                                }
                            }).show();
                            break;
                    }
                }
            }
        });
        mMenuRecyclerView.setAdapter(leftMenuAdapter);

    }

    private void upDateUserInfo() {

        if (BmobUser.getCurrentUser(MainActivity.this) != null) {

            BmobUser UserContent = BmobUser.getCurrentUser(MainActivity.this);

            tv_name.setText((String)UserContent.getObjectByKey(MainActivity.this,"nick"));
            tv_qianming.setText((String)UserContent.getObjectByKey(MainActivity.this, "qianming"));

            String imageurl= (String) UserContent.getObjectByKey(MainActivity.this,"iconUrl");//头像图片地址

            if (imageurl==null||imageurl.isEmpty())
            {
                Glide.with(MainActivity.this).load(R.mipmap.icon_user).asBitmap().centerCrop().animate(R.anim.fade).into(setRoundedImage(imageView));
            }else {
                Glide.with(MainActivity.this).load(imageurl).asBitmap().centerCrop().animate(R.anim.fade).into(setRoundedImage(imageView));
            }
        }
    }
    /**
     * 转换为圆角图片的方法
     * @param imageView 需要转换的imageview控件
     * */
    private BitmapImageViewTarget setRoundedImage(final ImageView imageView)
    {
        BitmapImageViewTarget bitmapImageViewTarget=new BitmapImageViewTarget(imageView)
        {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(MainActivity.this.getResources(), resource);
                roundedBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(roundedBitmapDrawable);
            }
        };
        return bitmapImageViewTarget;
    }




    private void zhuxiao(final View view) {
        if (BmobUser.getCurrentUser(MainActivity.this)!=null)
        {

            final AlertDialog alert=new AlertDialog.Builder(this).create();
            alert.show();
            Window window=alert.getWindow();
            window.setContentView(R.layout.alert_dialog_layout);
            window.setWindowAnimations(R.style.dialog_anim);
            Button zhuxiao= (Button) window.findViewById(R.id.id_dialog_zhuxiao);
            Button cancle= (Button) window.findViewById(R.id.id_dialog_quxiao);

            zhuxiao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BmobUser.logOut(MainActivity.this);   //清除缓存用户对象
                    tv_name.setText("");
                    tv_qianming.setText("");

                    if (BmobUser.getCurrentUser(MainActivity.this)==null)
                    {
                        imageView.setImageResource(R.mipmap.icon_user);
                    }
                    Snackbar.make(view,"账户已注销!",Snackbar.LENGTH_LONG).show();
//                    Toast.makeText(MainActivity.this,"账户已注销!",Toast.LENGTH_LONG).show();

                    leftMenuAdapter.noCurrentUser();//没有用户对象
                    alert.dismiss();
                }
            });

            //  取消操作
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

        }
    }

    private void VolleyGetNewsPaperTypes() {//得到全部类型

        StringRequest stringRequest= new StringRequest(Request.Method.GET, HtmlURL.NEWS_PAPER_TYPES, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String filterStirng= FilterHtml.filterHtml(s);//过滤字符

                Gson gson=new Gson();
                NewsPaperTypes newsPaperTypes=gson.fromJson(filterStirng, NewsPaperTypes.class);
                typeDatas=newsPaperTypes.getData();
                initAdapter(newsPaperTypes);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        stringRequest.setTag("MainActivity");
        MyApplication.getRequestQueue().add(stringRequest);
    }



    private void initToolBar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.id_toolbar);
        if (toolbar!=null) {
            /**设置主界面标题字体样式
             * **/
            toolbar.setTitle("");
            Typeface typeface=Typeface.createFromAsset(this.getAssets(),"fonts/deathblood.ttf");
            tv_toolbar_title.setTypeface(typeface);
            tv_toolbar_title.setText("Online Paper");


            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示侧滑菜单的图标
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.syncState();//设置为三横的图标
            mDrawerLayout.setDrawerListener(toggle);
        }
    }

    private void initView() {
        RefreshLayout= (MaterialRefreshLayout) findViewById(R.id.id_main_refresh);


        mRecyclerView = (RecyclerView) findViewById(R.id.id_recycleView);

        mDrawerLayout= (DrawerLayout) findViewById(R.id.id_drawerlayout);
        tv_toolbar_title= (TextView) findViewById(R.id.toolbar_title);
        tv_name= (TextView) findViewById(R.id.id_left_menu_name);
        tv_qianming= (TextView) findViewById(R.id.id_left_menu_qianming);
        tv_hour= (TextView) findViewById(R.id.id_main_hour);
        tv_PMorAM= (TextView) findViewById(R.id.id_main_PMorAM);

        mImageCycleView= (ImageCycleView) findViewById(R.id.id_main_vierpager);//首页轮播控件
        mReboundScrollView= (ReboundScrollView) findViewById(R.id.id_main_scrollview);
        mReboundScrollView.smoothScrollTo(0,0);//将scrollview起始位置设为顶部

        /**
         * 设置时间的显示
         *@先执行setTime()一次然后
         * @用handler实现计时器，每分钟更新一次时间，不可见时停止
         * */
        setTime();
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                setTime();
                handler.postDelayed(this,1000);
            }

        };
        handler.postDelayed(runnable,1000);//1秒执行一次


    }
    /**获取时间数据
     * @hour 小时
     * @apm  apm=0 表示上午，apm=1表示下午。
     * */
    private void setTime() {
        long timemillis=System.currentTimeMillis();
        Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(timemillis);
        String hour=String.valueOf(mCalendar.get(Calendar.HOUR));
        String minute=String.valueOf(mCalendar.get(Calendar.MINUTE));
        int apm=mCalendar.get(Calendar.AM_PM);
        if (Integer.valueOf(hour)<10){
            hour="0"+hour;
        }
        if(Integer.valueOf(minute)<10){
            minute="0"+minute;
        }
        tv_hour.setText(hour+":"+minute);
        if (apm==0){
            tv_PMorAM.setText("AM");
        }else if(apm==1){
            tv_PMorAM.setText("PM");
        }
    }


    private void initAdapter(NewsPaperTypes newsPaperTypes) {



        mMainAdapter = new MainAdapter(this,newsPaperTypes);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        AlphaAnimatorAdapter alphaAnimatorAdapter=new AlphaAnimatorAdapter(mMainAdapter,mRecyclerView);//淡入动画
        alphaAnimatorAdapter.getViewAnimator().setInitialDelayMillis(500);//延长动画时间

        mMainAdapter.setRecyItemOnclick(this);
        mRecyclerView.setAdapter(alphaAnimatorAdapter);



    }

    private void VolleyGetBanner() {
        listurl= new ArrayList<>();
        listurl.add("http://image2.sina.com.cn/dy/c/p/2007-01-18/U1565P1T1D12074936F21DT20070118224118.jpg");
        listurl.add("http://www.wokeji.com/shouye/shipin/201605/W020160524632769348038.png");
        listurl.add("http://www.ecns.cn/hd/2016/05/25/1d0dccb881dd49289ba931e41de81665.jpg");
        listurl.add("http://xh.xhby.net/60/res/1/20091001/32501254369124937.jpg");
        String[] strings={"现代快报","科技日报","北京晨报","扬子晚报"};


        mImageCycleView.setImageResources(listurl,strings, imageCycleViewListener);
    }



    private ImageCycleView.ImageCycleViewListener imageCycleViewListener = new ImageCycleView.ImageCycleViewListener() {

        @Override
        public void onImageClick(int position, View imageView) {
//            Toast.makeText(getApplicationContext(), "当前页卡->"+position, Toast.LENGTH_LONG).show();
            switch (position)
            {
                case 0:
                    Intent intent=new Intent(MainActivity.this,SearchNewsPaperResultActivity.class);
                    intent.putExtra("activity","MainActivity");
                    intent.putExtra("NAME","现代快报");
                    startActivity(intent);
                    break;
                case 1:
                    Intent intent1=new Intent(MainActivity.this,SearchNewsPaperResultActivity.class);
                    intent1.putExtra("activity","MainActivity");
                    intent1.putExtra("NAME","科技日报");
                    startActivity(intent1);
                    break;
                case 2:
                    Intent intent2=new Intent(MainActivity.this,SearchNewsPaperResultActivity.class);
                    intent2.putExtra("activity","MainActivity");
                    intent2.putExtra("NAME","北京晨报");
                    startActivity(intent2);
                    break;
                case 3:
                    Intent intent3=new Intent(MainActivity.this,SearchNewsPaperResultActivity.class);
                    intent3.putExtra("activity","MainActivity");
                    intent3.putExtra("NAME","扬子晚报");
                    startActivity(intent3);
                    break;
            }
        }


        @Override
        public void displayImage(String imageURL, ImageView imageView)
        {
            Glide.with(MainActivity.this)
                    .load(imageURL)//下载地址
//                    .thumbnail(0.1f)//加载时显示缩略图
                    .error(R.mipmap.image_cycle_view_error)//下载失败显示图
                    .into(imageView);
        }
    };


    /**
     * 监听返回键是否被按下**/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN)//返回键按键被按下
        {
            //System.currentTimeMillis()  当前时间与协调世界时 1970 年 1 月 1 日午夜之间的时间差（以毫秒为单位测量）。
            if(System.currentTimeMillis()-EXIT_TIME>2500)//第一次按下必定通过，第二次按下则判断两次返回键的时间差
            {
                Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
                EXIT_TIME=System.currentTimeMillis();//吧Exit_Time设置为当前时间
            }
            else
            {
                MyActivityStackManager.getInstance().ExitApplication(this);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onItemClick(View view, int postion) {
        Intent intent=new Intent(this,NewsActivity.class);
        intent.putExtra("TYPE_DATA",typeDatas.get(postion).getPresstype());
        startActivity(intent);
//        Toast.makeText(this,String.valueOf(postion),Toast.LENGTH_LONG).show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search)
        {
            startActivity(new Intent(MainActivity.this,SearchActivity.class));
        }else if (id==R.id.action_baokan)
        {
            startActivity(new Intent(MainActivity.this, SearchAllNewsPaperActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageCycleView.pushImageCycle();
    }



    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getRequestQueue().cancelAll("MainActivity");

//        Toast.makeText(this,"stop",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);//停止计时器
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTime();//回到当前activity时刷新时间
        if (BmobUser.getCurrentUser(MainActivity.this)!=null)
        {
            leftMenuAdapter.haveCurrentUser();
            mMenuRecyclerView.setAdapter(leftMenuAdapter);
        }
    }
}

