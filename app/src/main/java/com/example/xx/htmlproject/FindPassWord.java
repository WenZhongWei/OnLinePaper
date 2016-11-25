package com.example.xx.htmlproject;

import App.MyActivityStackManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;
import utils.HideKeyBoard;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class FindPassWord extends Activity implements OnClickListener{

	private CardView btn_Reset_ok;

	private EditText et_reset_email;

	private String reset_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_password_layout);
		/**把当前activity加入管理队列中*/
		MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
		myActivityStackManager.addActivity(FindPassWord.this);

		init();


	}



	private void init() {
		// TODO Auto-generated method stub
		btn_Reset_ok= (CardView) findViewById(R.id.btn_reset_ok);
		et_reset_email=(EditText) findViewById(R.id.et_reset_email);


		btn_Reset_ok.setOnClickListener(this);
		et_reset_email.setOnClickListener(this);



	}



	/**各个按键的监听**/
	@Override
	public void onClick(final View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{

		case R.id.btn_reset_ok:
			reset_email=et_reset_email.getText().toString().trim();

			if(reset_email.equals("")||reset_email.length()==0)
			{
				Snackbar.make(v,"请填写你的邮箱!",Snackbar.LENGTH_LONG).show();
				return;
			}
			BmobUser.resetPasswordByEmail(getApplicationContext(), reset_email, new ResetPasswordByEmailListener() {

				@Override
				public void onSuccess() 
				{
					et_reset_email.setText("");
                    HideKeyBoard.hide(FindPassWord.this);//隐藏键盘

					Snackbar.make(v,"密码重置邮件已发送,请按照邮件步骤重置密码!",Snackbar.LENGTH_INDEFINITE).setAction("确定", new OnClickListener() {
						@Override
						public void onClick(View v) {
							//void
						}
					}).show();
				}

				@Override
				public void onFailure(int arg0, String e) 
				{
					et_reset_email.setText("");
                    HideKeyBoard.hide(FindPassWord.this);//隐藏键盘

					Snackbar.make(v,"密码重置失败，请重试！",Snackbar.LENGTH_INDEFINITE).setAction("确定", new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}).show();
				}
			});

			break;

		default:
			break;
		}
	}

}
