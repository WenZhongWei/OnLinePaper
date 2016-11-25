package App;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by haiyuan 1995 on 2016/4/23.
 */
public class MyApplication extends Application {
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue= Volley.newRequestQueue(getApplicationContext());//new 一个VOLLEY请求队列
    }

    public static RequestQueue getRequestQueue()
    {
        return mRequestQueue;
    }
}
