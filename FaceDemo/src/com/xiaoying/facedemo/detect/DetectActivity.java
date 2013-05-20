/*
 * 文件名：DetectActivity.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-5-17
 * 修改人：xiaoying
 * 修改时间：2013-5-17
 * 版本：v1.0
 */
package com.xiaoying.facedemo.detect;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.xiaoying.facedemo.MainActivity;
import com.xiaoying.facedemo.MainApplication;
import com.xiaoying.facedemo.R;
import com.xiaoying.facedemo.utils.BitmapUtil;
import com.xiaoying.facedemo.utils.FileUtil;
import com.xiaoying.facedemo.utils.LogUtil;
import com.xiaoying.facedemo.widget.MarkFaceView;
import com.xiaoying.facedemo.widget.TitleBar;
import com.xiaoying.faceplusplus.api.cliet.Client;
import com.xiaoying.faceplusplus.api.config.RespConfig;
import com.xiaoying.faceplusplus.api.entity.Face;
import com.xiaoying.faceplusplus.api.entity.request.face.DetectReq;
import com.xiaoying.faceplusplus.api.entity.response.face.DetectResp;
import com.xiaoying.faceplusplus.api.service.FaceService;

/**
 * 功能：人脸识别的Activity
 * @author xiaoying
 *
 */
public class DetectActivity extends Activity {
	
	private String tag = DetectActivity.class.getSimpleName();
	
	public static final float OUTER_WIDTH = 15f;
	
	public static final float INER_WIDTH = 4f;
	
	public static final float INER_OUTER_DIST = 5f;
	
	private TitleBar mTitleBar = null;
	
	private MarkFaceView mMarkView = null;
	
	private Bitmap mBitmap = null;
	
	private int mInSampleSize = 1;
	
	private String mBitmapPath = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detect);
		
		initView();
		
		initData();
		
		DisplayMetrics m = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(m);
		
		mInSampleSize = BitmapUtil.calculateInSampleSize(mBitmapPath, m.widthPixels, m.heightPixels);
		
		mBitmap = BitmapUtil.loadBitmap(mBitmapPath, mInSampleSize);
		LogUtil.w(tag, "Bitmap size++++>>>(" + mBitmap.getWidth() + ", " + mBitmap.getHeight()  + ")");
		mMarkView.setBitmap(mBitmap);
	}
	
	
	private void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		
		mMarkView = (MarkFaceView) findViewById(R.id.mfv_mark_face);
		
		
		mTitleBar.setTitle(R.string.detect_face);
		mTitleBar.setLeftButton(R.string.backe, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mTitleBar.setRightButton(R.string.upload_detect, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				File file = new File(mBitmapPath);
				if(FileUtil.getFileSize(mBitmapPath) < 3 * 1024 * 1024) {
					Client client = new Client(MainApplication.APP_KEY, MainApplication.APP_SECRET);
					new UploadImage(client).execute(file);
				} else {
					Toast.makeText(DetectActivity.this, "图片不能大于3M", Toast.LENGTH_LONG).show();
				}
			}
		});
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
	
	
	private class UploadImage extends AsyncTask<File, Void, DetectResp> {
		
		private Client mClient = null;
		
		public UploadImage(Client client) {
			this.mClient = client;
		}

		@Override
		protected DetectResp doInBackground(File... params) {
			DetectReq req = new DetectReq(params[0]);
			req.setAsync(false);
			FaceService service = new FaceService(mClient);
			try {
				return service.detect(req);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(DetectResp result) {
			super.onPostExecute(result);
			if(result != null) {
				LogUtil.i(tag, result);
				if(result.getError_code() == RespConfig.RESP_OK) {
					List<Face> faces = result.getFace();
					mMarkView.markFaces(faces);
					mMarkView.setOnFaceClickListener(new MarkFaceView.OnFceClickedListener() {
						@Override
						public void onFaceClicked(Face face, int position) {
							LogUtil.w(tag, "Position++++++++++>>>" + position);
							LogUtil.w(tag, face);
						}
					});
				} else {
					Toast.makeText(DetectActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
}
