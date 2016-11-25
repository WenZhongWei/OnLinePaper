package com.example.xx.htmlproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import App.MyActivityStackManager;
import Data.MyUser;
import Data.Person;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class PersonInfoActivity extends Activity implements OnClickListener{
	private ScrollView scrollView;

    private CardView cardView;

    private ImageView imageView;

	private TextView tv_person_cancle;
	private TextView tv_person_ok;

	private EditText et_person_nick;
	private EditText et_person_sex;
	private EditText et_person_age;
	private EditText et_person_qianming;
	private EditText et_person_school;
	private EditText et_person_home;
	private EditText et_person_zhiye;
	private EditText et_person_phone;
	private EditText et_person_shuoming;
	private EditText et_person_email;

	private String nick;
	private String sex;
	private String qianming;
	private String school;
	private String home;
	private String zhiye;
	private String phone;
	private String shuoming;

	Integer age;

	private CustomProgressDialog pd;

    private static final int PHOTO_SUCCESS=1;//从图库中获取

    private static final int CAMERA_SUCCESS=2;//从相机中拍摄

    private static final int PHOTO_REQUEST_CUT=3;//裁剪后返回的图片

    private String picPath="";//图片裁剪后的绝对路径

    private String bmobUrl="";//上传到bmob后文件的url

    private boolean flag=false;//判断是否有选择图片，默认为无

    private AlertDialog alert;//弹窗

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_layout);

        /**把当前activity加入管理队列中*/
        MyActivityStackManager myActivityStackManager=MyActivityStackManager.getInstance();
        myActivityStackManager.addActivity(PersonInfoActivity.this);

		initView();
        initUser();

        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                alert=new AlertDialog.Builder(PersonInfoActivity.this).create();
                alert.show();
                Window window=alert.getWindow();
                window.setContentView(R.layout.select_picture_layout);
                Button tuku= (Button) window.findViewById(R.id.id_select_tuku);
                Button camera= (Button) window.findViewById(R.id.id_select_photo);

                tuku.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getImage();
                    }
                });

                camera.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getImageByCamera();
                    }
                });
            }


        });


	}



    private void initUser() {
        BmobUser isLogin=BmobUser.getCurrentUser(PersonInfoActivity.this);

        if(isLogin!=null)//有登录对象的时候，吧个人资料下载下来赋值
        {
            setContent();
        }
    }

    private void getImageByCamera() {
        Intent getImage=new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(getImage,CAMERA_SUCCESS);
    }

    private void getImage() {
//		Intent getImage=new Intent(Intent.ACTION_GET_CONTENT);
        //		getImage.addCategory(Intent.CATEGORY_OPENABLE);
        //		getImage.setType("image/*");
        //旧版的获取图片uri不能在4.3以上系统使用

        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent,PHOTO_SUCCESS);
    }

    /**照片返回结果**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode==RESULT_OK)
        {
            if(PHOTO_SUCCESS==requestCode) {
                //获得图片的URI
//                ContentResolver contentResolver = getContentResolver();
                Uri uri = data.getData();
                cutImage(uri);

            }else if(requestCode==CAMERA_SUCCESS) {

                Bundle extras = data.getExtras();
                Bitmap originalBitmap1 = (Bitmap) extras.get("data");
                Uri imageUri;
                if (data.getData() != null) {
                    imageUri = Uri.parse(data.getDataString());
                } else {
                    imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), originalBitmap1, null, null));
                }

                cutImage(imageUri);

            }else if (requestCode==PHOTO_REQUEST_CUT)
            {
                Bitmap bitmap = data.getParcelableExtra("data");
                Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                picPath = getImagePath(imageUri, null);//获取图片的绝对路径，用于上传到网上
                System.out.println(picPath);
                Glide.with(PersonInfoActivity.this).load(picPath).into(imageView);

                flag=true;//选择了图片
                alert.dismiss();

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void cutImage(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private String getImagePath(Uri uri, String seletion) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, seletion, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();

        }
        return path;
    }

    private void setContent()
	{
		// TODO Auto-generated method stub

		BmobUser UserContent=BmobUser.getCurrentUser(PersonInfoActivity.this);
        String imageurl= (String) UserContent.getObjectByKey(PersonInfoActivity.this,"iconUrl");//头像图片地址

        if (imageurl==null||imageurl.isEmpty())
        {
            Glide.with(PersonInfoActivity.this).load(R.mipmap.icon_user).asBitmap().centerCrop().into(imageView);//设置默认头像
        }else {
            Glide.with(PersonInfoActivity.this).load(imageurl).asBitmap().centerCrop().into(imageView);
        }


		nick=(String)UserContent.getObjectByKey(this, "nick");
		sex=(String) UserContent.getObjectByKey(this, "sex");
		qianming=(String)UserContent.getObjectByKey(this, "qianming");
		school=(String)UserContent.getObjectByKey(this, "school");
		home=(String)UserContent.getObjectByKey(this, "home");
		zhiye=(String)UserContent.getObjectByKey(this, "zhiye");
		phone=(String)UserContent.getObjectByKey(this, "phone");
		shuoming=(String)UserContent.getObjectByKey(this, "shuoming");


		et_person_nick.setText(nick);
		if(UserContent.getObjectByKey(this, "age")==null)
		{
			et_person_age.setText("0");
		}
		else
		{
			et_person_age.setText(UserContent.getObjectByKey(this, "age").toString());
		}
		et_person_sex.setText(sex);
		et_person_qianming.setText(qianming);
		et_person_school.setText(school);
		et_person_home.setText(home);
		et_person_zhiye.setText(zhiye);
		et_person_phone.setText(phone);
		et_person_shuoming.setText(shuoming);
		et_person_email.setText((String)UserContent.getObjectByKey(this, "email"));


	}
	private void initView() {
		/**获取到scrollview的焦点**/
		scrollView=(ScrollView) findViewById(R.id.scrollView);
		scrollView.setFocusable(true);
		scrollView.setFocusableInTouchMode(true);
		scrollView.requestFocus();
		scrollView.smoothScrollTo(0, 20);//返回顶部

		tv_person_cancle=(TextView) findViewById(R.id.tv_person_cancle);
		tv_person_ok=(TextView) findViewById(R.id.tv_person_ok);

		et_person_nick=(EditText) findViewById(R.id.et_person_nick);
		et_person_sex=(EditText) findViewById(R.id.et_person_sex);
		et_person_age=(EditText) findViewById(R.id.et_person_age);
		et_person_qianming=(EditText) findViewById(R.id.et_person_qianming);
		et_person_school=(EditText) findViewById(R.id.et_person_school);
		et_person_home=(EditText) findViewById(R.id.et_person_home);
		et_person_zhiye=(EditText) findViewById(R.id.et_person_zhiye);
		et_person_phone=(EditText) findViewById(R.id.et_person_phone);
		et_person_shuoming=(EditText) findViewById(R.id.et_person_shuoming);
		et_person_email=(EditText) findViewById(R.id.et_person_email);

        cardView= (CardView) findViewById(R.id.id_person_cardview);
        imageView= (ImageView) findViewById(R.id.id_person_picture);

		tv_person_cancle.setOnClickListener(this);
		tv_person_ok.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.tv_person_ok:
                pd=CustomProgressDialog.createDialog(PersonInfoActivity.this);
                pd.setMessage("更新中，请稍等...");
                pd.show();
				UserUpdate();
				break;

			case R.id.tv_person_cancle:
				finish();
				break;

			default:
				break;
		}
	}
	/**信息更新方法**/
	private void UserUpdate() {
		// TODO Auto-generated method stub
		nick=et_person_nick.getText().toString().trim();
		sex=et_person_sex.getText().toString().trim();

		if(et_person_age.getText().toString().trim().equals(""))
		{
			age=0;
			et_person_age.setText("0");
		}
		else
		{
			age=Integer.valueOf(et_person_age.getText().toString().trim());
		}
		qianming=et_person_qianming.getText().toString().trim();
		school=et_person_school.getText().toString().trim();
		home=et_person_home.getText().toString().trim();
		zhiye=et_person_zhiye.getText().toString().trim();
		phone=et_person_phone.getText().toString().trim();
		shuoming=et_person_shuoming.getText().toString().trim();

            /**跟新数据**/
				spandTimeMethod();//耗时操作

	}
	protected void spandTimeMethod() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
        MyUser bmobUser=BmobUser.getCurrentUser(this, MyUser.class);


        bmobUser.setNick(nick);
        bmobUser.setSex(sex);
        bmobUser.setAge(age);
        bmobUser.setQianming(qianming);
        bmobUser.setSchool(school);
        bmobUser.setHome(home);
        bmobUser.setZhiye(zhiye);
        bmobUser.setPhone(phone);
        bmobUser.setShuoming(shuoming);


        uploadIcon();

        bmobUser.update(PersonInfoActivity.this, bmobUser.getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(PersonInfoActivity.this, "信息已更新", Toast.LENGTH_LONG).show();
                pd.dismiss();
                finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(PersonInfoActivity.this, "更新失败"+arg1, Toast.LENGTH_LONG).show();
                System.out.println(arg1);
                pd.dismiss();
                finish();
			}
		});


	}

    private void uploadIcon() {

        if (flag==false) {
            System.out.println("没有选择图片");
        }else if (flag==true) {
            final MyUser bmobUser = BmobUser.getCurrentUser(this, MyUser.class);
            final Person person = new Person();

            person.setUserName(bmobUser);//设置头像与用户关联

            final BmobFile pictureFile = new BmobFile(new File(picPath));
            pictureFile.uploadblock(PersonInfoActivity.this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    System.out.println(pictureFile.getUrl());
                    bmobUrl = pictureFile.getFileUrl(PersonInfoActivity.this);//获取到上传到BMOB图片的URL地址
                    person.setPictureFile(pictureFile);
                    person.save(PersonInfoActivity.this);
                    System.out.println("头像上传成功");

                    bmobUser.setIconUrl(bmobUrl);//保存图片地址到用户对象
                    bmobUser.update(PersonInfoActivity.this);
                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(PersonInfoActivity.this, s, Toast.LENGTH_LONG).show();
                    System.out.println("头像上传失败");
                }
            });

//            System.out.println(bmobUrl);

        }

    }

}
