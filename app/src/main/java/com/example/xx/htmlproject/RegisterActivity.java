package com.example.xx.htmlproject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import App.MyActivityStackManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends Activity {
	private CardView btn_registers;

	private EditText et_re_name;
	private EditText et_re_password;
	private EditText et_re_email;
	private CustomProgressDialog pd;
	private String re_user_name,re_user_password,re_user_email;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);

		/**把当前activity加入管理队列中*/
		MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
		myActivityStackManager.addActivity(RegisterActivity.this);

		initView();


		btn_registers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub

				re_user_name=et_re_name.getText().toString().trim();
				re_user_password=et_re_password.getText().toString().trim();
				re_user_email=et_re_email.getText().toString().trim();
				if(re_user_name.equals("")){
					Snackbar.make(v,"请填写你的账号!",Snackbar.LENGTH_LONG).show();
					return;
				}

				if(re_user_password.equals("")){
					Snackbar.make(v,"请填写你的密码!",Snackbar.LENGTH_LONG).show();
					return;
				}
				if(re_user_email.equals(""))
				{
					Snackbar.make(v,"请填写你的邮箱!",Snackbar.LENGTH_LONG).show();
					return;
				}
				pd=CustomProgressDialog.createDialog(RegisterActivity.this);
				pd.setMessage("注册中,请稍等...");
				pd.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						NeedTime();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}

					private void NeedTime() {
						// TODO Auto-generated method stub
						BmobUser u = new BmobUser();
						u.setUsername(re_user_name);
						u.setPassword(re_user_password);
						u.setEmail(re_user_email);
						u.signUp(getApplication(), new SaveListener() {

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
                                pd.dismiss();
                                Snackbar.make(v,"注册成功,点击确定跳转到登录界面",Snackbar.LENGTH_INDEFINITE).setAction("确定", new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        BmobUser.logOut(RegisterActivity.this);
//                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();

							}

							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
                                Snackbar.make(v,"注册失败:"+arg1,Snackbar.LENGTH_LONG).show();
							}
						});
					}
				}).start();
				
			}
		});
		
	}

	private void initView() {

		btn_registers= (CardView) findViewById(R.id.btn_registers);
		et_re_name=(EditText) findViewById(R.id.et_re_name);
		et_re_password=(EditText) findViewById(R.id.et_re_password);
		et_re_email=(EditText) findViewById(R.id.et_re_email);
	}

	Handler handler=new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			pd.dismiss();
		}
	};

}
