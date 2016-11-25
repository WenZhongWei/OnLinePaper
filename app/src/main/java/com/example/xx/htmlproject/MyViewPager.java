package com.example.xx.htmlproject;


import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/5/13 0013.
 */
public class MyViewPager extends ViewPager{


    /**
     * 接口
     */
    public interface LifeCycle{

    }

    /**
     * 恢复
     */
    public static final int RESUME = 0;

    /**
     *  暂停
     */
    public static final int PAUSE = 1;

    /**
     * 销毁
     */
    public static final int DESTROY = 2;

    /**
     * 生命周期状态
     */
    private  int mLifeCycle = RESUME;

    /**
     * 是否正在触摸状态，用以防止触摸滑动和自动轮播冲突
     */
    public static  boolean mIsTouching = false;

    /**
     * 轮播定时器
     */
    private ScheduledExecutorService mCarouselTimer;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 外部调用的方法设置生命周期
     * @param lifeCycle RESUME，PAUSE，DESTROY 恢复，暂停，销毁
     */
    public void setLifeCycle(int lifeCycle){
        this.mLifeCycle = lifeCycle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN://按下
                System.out.println("onTouchEvent:"+"正在触摸");
            case MotionEvent.ACTION_MOVE://移动
                mIsTouching = true;//正在触摸
//                shutdownTimer();//关闭定时器
                System.out.println("onTouchEvent:"+"正在移动");
                break;
            case MotionEvent.ACTION_CANCEL://取消
            case MotionEvent.ACTION_UP://放开
//                mIsTouching = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsTouching = false;//没有触摸
                        System.out.println("onTouchEvent:"+"没有触摸");
                    }
                },10000);
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        shutdownTimer();//关闭定时器
        //实例化定时器
        mCarouselTimer = Executors.newSingleThreadScheduledExecutor();
        mCarouselTimer.scheduleAtFixedRate(new Runnable(){
            @Override
            public void run() {
                switch (mLifeCycle){
                    case RESUME://恢复
                        if (!mIsTouching && getAdapter() != null && getAdapter().getCount() > 1){
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    setCurrentItem(getCurrentItem() +1);

                                }
                            });
                        }
                        break;
                    case PAUSE://暂停
                        break;
                    case DESTROY://销毁
                        shutdownTimer();
                        break;
                }
            }
        },1000*10,1000*10, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        shutdownTimer();
    }

    /**
     * 关闭定时器
     */
    private void shutdownTimer() {
        //定时器不为空且定时器没有关闭时
        if (mCarouselTimer != null && mCarouselTimer.isShutdown() == false){
            //关闭定时器
            mCarouselTimer.shutdown();
        }
        mCarouselTimer = null;
    }
}


