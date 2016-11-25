package com.example.xx.htmlproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import App.MyActivityStackManager;
import StartActivityAnim.DepthPageTransformer;

public class AppGuideActivity extends Activity {
	
	private ViewPager mViewPager;
	private Button startApp;
	
	
	private int[]mImgIds = new int[]{R.mipmap.guide_image1,R.mipmap.guide_image2,R.mipmap.guide_image3};

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_guide_layout);
		        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		/**把当前activity加入管理队列中*/
		MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
		myActivityStackManager.addActivity(AppGuideActivity.this);


		initView();
        setAnim();
        

    }
	private void setAnim() {
	 //设置viewpager动画效果
			mViewPager.setPageTransformer(true,new DepthPageTransformer());
//	       mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
//	       mViewPager.setPageTransformer(true, new RotateDownPageTransformer());

	}
	private void initView() {
		startApp = (Button) findViewById(R.id.startApp);
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);


		startApp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}
		});



		mViewPager.setAdapter(new PagerAdapter() {

			@Override
			public Object instantiateItem(ViewGroup container, final int position) {
				ImageView imageView = new ImageView(AppGuideActivity.this);
				imageView.setImageResource(mImgIds[position]);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				container.addView(imageView);

				return imageView;

			}

			@Override
			public void destroyItem(ViewGroup container, int position,
									Object object) {
				container.removeView((View) object);
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				// TODO Auto-generated method stub
				return view == object;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mImgIds.length;
			}
		});
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				if (position == (mImgIds.length-1)) {
					startApp.setVisibility(View.VISIBLE);
				}else {
					startApp.setVisibility(View.GONE);
				}
			}


			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}



}
 