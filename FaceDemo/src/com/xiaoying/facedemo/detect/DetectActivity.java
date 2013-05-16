
package com.xiaoying.facedemo.detect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaoying.facedemo.MainActivity;
import com.xiaoying.facedemo.R;
import com.xiaoying.facedemo.utils.LogUtil;

public class DetectActivity extends Activity {
	
	private String tag = DetectActivity.class.getSimpleName();

	private ImageView mImageView = null;
	
	private Bitmap mBitmap = null;
	
	private String mBitmapPath = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detect);
		
		initView();
		
		initData();
	}
	
	private void initView() {
		mImageView = (ImageView) findViewById(R.id.iv_image);
	}
	
	private void initData() {
		Intent intent = getIntent();
		if(intent.hasExtra(MainActivity.EXTRA_IMAGE)) {
			mBitmapPath = intent.getStringExtra(MainActivity.EXTRA_IMAGE);
			LogUtil.i(tag, "+++>>>" + mBitmapPath);
		} else {
			Toast.makeText(this, R.string.pick_image_err, Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
