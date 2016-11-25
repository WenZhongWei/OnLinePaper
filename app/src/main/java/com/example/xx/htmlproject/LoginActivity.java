package com.example.xx.htmlproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import App.MyActivityStackManager;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    public static String APPID = "62fa233d7333debb873e0e38bd4ec4ce";//bmob应用ID

    private Button btn_find_password;

    private CardView btn_ok,btn_zhuce;

    private EditText et_name,et_password;

    private CheckBox cb_remember;

    private SharedPreferences config;

    private String user_name,user_password;

    private CustomProgressDialog pd;

    private String activity="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        /**把当前activity加入管理队列中*/
        MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
        myActivityStackManager.addActivity(LoginActivity.this);
        if (getIntent()!=null) {
            if (!getIntent().getExtras().isEmpty() && getIntent().getExtras().getString("activity").equals("CommentActivity")) {
                activity = getIntent().getExtras().getString("activity");
            }
        }

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Bmob.initialize(getApplicationContext(), APPID);//初始化BMOB
        config=getSharedPreferences("config", MODE_PRIVATE);
        BmobUser.getCurrentUser(this).logOut(this);
        initView();



        //是否记住了密码
        boolean isChecked=config.getBoolean("ischeck", false);
        if(isChecked)
        {
            et_name.setText(config.getString("user_name", ""));
            et_password.setText(config.getString("user_password", ""));
        }
        cb_remember.setChecked(isChecked);//设置checkbox是否打钩



        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                user_name=et_name.getText().toString().trim();
                user_password=et_password.getText().toString().trim();
                SharedPreferences.Editor edit=config.edit();//得到编辑器
                boolean isChecked=cb_remember.isChecked();//得到选中状态
                edit.putBoolean("ischeck", isChecked);

                if(isChecked)
                {
                    edit.putString("user_name",user_name );
                    edit.putString("user_password", user_password);
                }
                else
                {
                    edit.remove("user_name").remove("user_password");
                }
                edit.apply();


                if(user_name.equals("")){
                    Toast.makeText(LoginActivity.this, "请填写你的账号", Toast.LENGTH_LONG).show();
                    return;
                }

                if(user_password.equals("")){
                    Toast.makeText(LoginActivity.this, "请填写你的密码", Toast.LENGTH_LONG).show();
                    return;
                }

                pd=CustomProgressDialog.createDialog(LoginActivity.this);
                pd.show();
				/* 开启一个新线程，在新线程里执行耗时的方法 */
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        spandTimeMethod();//耗时操作
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    private void spandTimeMethod() {
                        BmobUser user=new BmobUser();//新建用户对象
                        user.setUsername(user_name);
                        user.setPassword(user_password);

                        user.login(getApplication(), new SaveListener() {

                            @Override
                            public void onSuccess() {
                                Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_LONG).show();
                                pd.dismiss();
                                if (activity.equals("CommentActivity")) {
                                    finish();//返回评论界面
                                }else {

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();//返回主界面
                                }
                            }

                            @Override
                            public void onFailure(int arg0, String arg1) {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this, "请检查你的账号密码或网络情况！", Toast.LENGTH_LONG).show();
                            }

                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        btn_zhuce.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        btn_find_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,FindPassWord.class);

                startActivity(intent);
            }
        });

    }

    private void initView() {
        btn_ok= (CardView) findViewById(R.id.btn_ok);
        btn_zhuce= (CardView) findViewById(R.id.btn_zhuce);
        btn_find_password=(Button) findViewById(R.id.btn_find_password);

        cb_remember=(CheckBox) findViewById(R.id.cb_remeber);

        et_name=(EditText) findViewById(R.id.et_name);
        et_password=(EditText) findViewById(R.id.et_password);
    }
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		pd.dismiss();
//	}
}
