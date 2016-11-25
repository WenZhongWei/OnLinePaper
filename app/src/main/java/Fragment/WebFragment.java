package Fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.xx.htmlproject.MyViewPager;
import com.example.xx.htmlproject.R;

/**
 * Created by XX on 2016/5/11.
 */
public class WebFragment extends Fragment {
    private WebView webView ;
    private String url;//网页地址
    private	int Density; //屏幕尺寸
    private ProgressBar mProgressBar;
    @SuppressWarnings("deprecation")
    @SuppressLint({"NewApi", "SetJavaScriptEnabled" }) @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.webpager, container, false);

        webView = (WebView) view.findViewById(R.id.wedview);
         mProgressBar= (ProgressBar) view.findViewById(R.id.id_web_content_ProgressBar);

        url = (String) this.getArguments().get("url");
        Density = this.getArguments().getInt("Density");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {//不使用系统浏览器浏览
                view.loadUrl(url);
                return true;
            }
            /**404错误处理*/
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                view.stopLoading();
                 view.clearView();
                view.loadUrl("file:///android_asset/web404.jpg");
            }
        });
        WebSettings webSettings= webView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

//      DisplayMetrics metrics = new DisplayMetrics();
//      getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//      int mDensity = metrics.densityDpi;
        /**
         * 按照不同设备的DPI来设置缩放比率
         * */
        if (Density <= 240) {
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
        }else if (Density > 240&&Density <= 320) {

            webView.setInitialScale(50);//缩放等级
        }else if (Density >=480) {

            webView.setInitialScale(100);//缩放等级
        }

        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);//缓存
        }
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);//缓存
        webSettings.setJavaScriptEnabled(true);// 设置支持javascript脚本
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//javascript能自动打开窗口
        webSettings.setBuiltInZoomControls(true);//显示缩放按钮
        webSettings.setSupportZoom(true);//支持缩放
        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);


        //设置进度条
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);//设置进度
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        MyViewPager.mIsTouching = true;
                        System.out.println("webview:" + "正在触摸");
                        break;
                    case MotionEvent.ACTION_CANCEL://取消
                    case MotionEvent.ACTION_UP://放开
                        MyViewPager.mIsTouching = false;//没有触摸
                        System.out.println("webview:" + "没有触摸");
                        break;
                }
                return false;
            }
        });
        webView.loadUrl(url);
        Log.i("Fragment", "Fragment创建了");
        return view;
    }



    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i("Fragment", "Fragment销毁了");
    }
    /**
     * 静态工厂方法需要一个int型的值来初始化fragment的参数，
     * 然后返回新的fragment到调用者
     * url:网页地址
     * Density：屏幕尺寸
     */
    public static WebFragment newInstance(String url, int Density) {
        WebFragment f = new WebFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putInt("Density", Density);
        f.setArguments(args);
        return f;
    }
}

