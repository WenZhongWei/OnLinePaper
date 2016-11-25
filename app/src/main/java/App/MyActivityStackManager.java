package App;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * Created by haiyuan 1995 on 2016/5/21.
 * 应用程序的ACtivity管理
 *
 */

public class MyActivityStackManager {
    private static Stack<Activity> sActivityStack;
    private static MyActivityStackManager sInstance;

    private MyActivityStackManager() {
    }
    /**单例模式 */
    public static MyActivityStackManager getInstance()
    {
        if (sInstance==null)
        {
            sInstance=new MyActivityStackManager();
        }
        return sInstance;
    }


    /**activity入栈**/
    public void addActivity(Activity activity)
    {
        if (sActivityStack==null)
        {
            sActivityStack=new Stack<Activity>();
        }
        sActivityStack.add(activity);
    }

    /**获取当前activity，即最后一个入栈的*/

    public Activity getCurrentActivity()
    {
        Activity activity=sActivityStack.lastElement();//得到栈中最后一个元素
        return activity;
    }

    /**结束当前activity*/
    public void finishCurrentActivity()
    {
        Activity activity=sActivityStack.lastElement();
        finishActivity(activity);
    }

    /**结束指定activity*/
    public void finishActivity(Activity activity)
    {
        if (activity!=null)
        {
            sActivityStack.remove(activity);
            activity.finish();
            activity=null;
        }
    }

    /**结束指定类名的activity*/
    public void finishActivity(Class<?> cls)
    {
        for (Activity activity:sActivityStack)
        {
            if (activity.getClass().equals(cls))//判断如果栈中有类名相同的activity，就结束
            {
                finishActivity(activity);
            }
        }
    }

    /**结束所有的activity*/
    public void finishAllActivity()
    {
        for (int i=0;i<sActivityStack.size();i++)
        {
            if (sActivityStack.get(i)!=null)
            {
                sActivityStack.get(i).finish();//遍历结束所有activity
            }
        }

        sActivityStack.clear();//清空栈对象
    }
        /**退出应用程序**/
        public void ExitApplication(Context context)
        {
            try {
                finishAllActivity();
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.restartPackage(context.getPackageName());
                System.exit(0);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }


}
