package com.example.xx.htmlproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by XX on 2016/5/26.
 */

public class AppStartActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences setting=getSharedPreferences("SHARE_APP_TAG", Activity.MODE_PRIVATE);
        boolean user_first=setting.getBoolean("FIRST",true);
        if (user_first)//第一次启动
        {
            setting.edit().putBoolean("FIRST",false).commit();
            startActivity(new Intent(AppStartActivity.this,AppGuideActivity.class));
            finish();
        }else {
            setContentView(R.layout.start_layout);
            imageView = (ImageView) findViewById(R.id.start_imageView);
            Glide.with(this).load(R.mipmap.start).animate(R.anim.hold).into(imageView);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                    finish();
                }
            }, 3000);
        }
    }
}
