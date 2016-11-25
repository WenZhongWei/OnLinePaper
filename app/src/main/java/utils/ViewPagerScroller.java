package utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;
 
/**
 * ViewPager 滚动速度设置
 * 
 * @author Tercel
 * 
 */
public class ViewPagerScroller extends Scroller {
    private int mScrollDuration = 1000;             // 滑动速度
 
    /**
     * 设置HTML页面切换速度
     * @param duration
     */
    public void setScrollDuration(int duration){
        this.mScrollDuration = duration;
    }
     
    public ViewPagerScroller(Context context) {
        super(context);
    }
 
    public ViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }
 
    public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }
 
    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }
 
    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }
 
     
     
    public void initViewPagerScroll(ViewPager viewPager) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");//反射得到对象
            mScroller.setAccessible(true);//打破封装
            mScroller.set(viewPager, this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
