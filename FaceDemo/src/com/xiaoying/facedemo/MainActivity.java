package com.xiaoying.facedemo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.xiaoying.facedemo.detect.DetectActivity;
import com.xiaoying.facedemo.utils.LogUtil;
import com.xiaoying.facedemo.utils.StringUtil;

public class MainActivity extends Activity implements View.OnClickListener {

	private String tag = MainActivity.class.getSimpleName();
	
	public static final String EXTRA_IMAGE = "extra_image";

	public static final int REQUEST_PICK_PIC = 1000;
	
	public static final int REQUEST_TAKE_PIC = 1001;
	
	private String mImagePath = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.btn_choose_pic).setOnClickListener(this);
		findViewById(R.id.btn_take_pic).setOnClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_PICK_PIC :
				if(resultCode == RESULT_OK) {
					if(data != null) {
						Uri uri = data.getData();
						if(uri != null) {
							LogUtil.i(tag, uri);
							LogUtil.e(tag, getImageUrl(uri));
							String image = getImageUrl(uri);
							gotoDetect(image);
						}
					}
				}
				break;
			case REQUEST_TAKE_PIC:
				if(resultCode == RESULT_OK && mImagePath != null) {
					gotoDetect(mImagePath);
					mImagePath = null;
				}
				break;
			default :
				break;
		}
	}
	
	
	private String getImageUrl(Uri uri) {
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(uri, null, null, null, null);
		if(c != null && c.moveToFirst()) {
			return c.getString(c.getColumnIndex("_data"));
		}
		return null;
	}
	
	private void pickImage() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_PICK_PIC);
	}
	
	private void takePic() {
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String imagePath = MainApplication.APP_PIC_PATH + StringUtil.createImageName();
			takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
			startActivityForResult(takePhotoIntent, REQUEST_TAKE_PIC);
		} else {
			Toast.makeText(this, "没有检测到SDcard，请检查SDcard！", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private void gotoDetect(String image) {
		Intent intent = new Intent(this, DetectActivity.class);
		intent.putExtra(EXTRA_IMAGE, image);
		startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_choose_pic:
				pickImage();
				break;
			case R.id.btn_take_pic:
				takePic();
				break;
			default :
				break;
		}
	}
	
}
