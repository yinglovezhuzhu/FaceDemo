
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
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaoying.facedemo.MainActivity;
import com.xiaoying.facedemo.MainApplication;
import com.xiaoying.facedemo.R;
import com.xiaoying.facedemo.utils.BitmapUtil;
import com.xiaoying.facedemo.utils.FileUtil;
import com.xiaoying.facedemo.utils.LogUtil;
import com.xiaoying.facedemo.widget.TitleBar;
import com.xiaoying.faceplusplus.api.cliet.Client;
import com.xiaoying.faceplusplus.api.config.RespConfig;
import com.xiaoying.faceplusplus.api.entity.Face;
import com.xiaoying.faceplusplus.api.entity.request.face.DetectReq;
import com.xiaoying.faceplusplus.api.entity.response.face.DetectResp;
import com.xiaoying.faceplusplus.api.service.FaceService;

public class DetectActivity extends Activity {
	
	private String tag = DetectActivity.class.getSimpleName();
	
	public static final float OUTER_WIDTH = 15f;
	
	public static final float INER_WIDTH = 4f;
	
	public static final float INER_OUTER_DIST = 5f;
	
	/** 内框和外框的间距 */
	private float mIODist = 8f;

	private TitleBar mTitleBar = null;
	
	private ImageView mImageView = null;
	
	private Bitmap mBitmap = null;
	
	private int mInSampleSize = 1;
	
	private String mBitmapPath = null;
	
	private float mOuterWidth = OUTER_WIDTH;
	/** 框透明度 */
	private int mAlpha = 130;
	/** 框颜色 */
	private int mBorderColor = Color.argb(mAlpha, 0x00, 0x9A, 0xD6);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detect);
		
		initView();
		
		initData();
		
		DisplayMetrics m = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(m);
		
		mInSampleSize = BitmapUtil.calculateInSampleSize(mBitmapPath, m.widthPixels, m.heightPixels);
		
		Bitmap bitmap = BitmapUtil.loadBitmap(mBitmapPath, mInSampleSize);
		mBitmap = bitmap.copy(Config.ARGB_8888, true);
		bitmap.recycle();
		
		LogUtil.w(tag, "Bitmap size++++>>>(" + mBitmap.getWidth() + ", " + mBitmap.getHeight()  + ")");
		
		mImageView.setImageBitmap(mBitmap);
		
	}
	
	
	private void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		mImageView = (ImageView) findViewById(R.id.iv_image);
		
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
				// TODO Auto-generated method stub
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
					Canvas canvas = new Canvas(mBitmap);
					Paint paint = new Paint();
					paint.setColor(mBorderColor);
					paint.setStyle(Style.STROKE);
					paint.setStrokeWidth(getStrokeWidth(mBitmap, mImageView, OUTER_WIDTH));
					Log.e("BBBB", "Stroke width+++++>>>" + paint.getStrokeWidth());
					canvas.drawBitmap(mBitmap, 0, 0, paint);
					int width = mBitmap.getWidth();
					int height = mBitmap.getHeight();
					for (Face face : faces) {
						LogUtil.w(tag, face);
						drawOuterRect(canvas, paint, face, width, height);
						drawInerRect(canvas, paint, face, width, height);
					}
					mImageView.setImageBitmap(mBitmap);
				} else {
					Toast.makeText(DetectActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	private void drawOuterRect(Canvas canvas, Paint paint, Face face, int bmWidth, int bmHeight) {
		PointF center = face.getCenter();
		float width = face.getWidth();
		float height = face.getHeight();
//		PointF eyeLeft = face.getEye_left();
//		canvas.drawPoint(bmWidth * (eyeLeft.x / 100), bmHeight * (eyeLeft.y / 100), paint);
//		PointF eyeRight = face.getEye_right();
//		canvas.drawPoint(bmWidth * (eyeRight.x / 100), bmHeight * (eyeRight.y / 100), paint);
		paint.setStrokeWidth(OUTER_WIDTH * getScale(mBitmap, mImageView));
		float left = (bmWidth * (center.x / 100) - bmWidth * (width / 100) / 2) - paint.getStrokeWidth();
		float top = (bmHeight * (center.y / 100) - bmHeight * (height / 100) / 2) - paint.getStrokeWidth();
		float right = (bmWidth * (center.x / 100) + bmWidth * (width / 100) / 2) + paint.getStrokeWidth();
		float bottom = (bmHeight * (center.y / 100) + bmHeight * (height / 100) / 2) + paint.getStrokeWidth();
		RectF rect = new RectF(left, top, right, bottom);
		canvas.drawRect(rect, paint);
	}
	
	private void drawInerRect(Canvas canvas, Paint paint, Face face, int bmWidth, int bmHeight) {
		PointF center = face.getCenter();
		float width = face.getWidth();
		float height = face.getHeight();
		paint.setStrokeWidth(INER_WIDTH * getScale(mBitmap, mImageView));
		float left = (bmWidth * (center.x / 100) - bmWidth * (width / 100) / 2) + mIODist;
		float top = (bmHeight * (center.y / 100) - bmHeight * (height / 100) / 2) + mIODist;
		float right = (bmWidth * (center.x / 100) + bmWidth * (width / 100) / 2) - mIODist;
		float bottom = (bmHeight * (center.y / 100) + bmHeight * (height / 100) / 2) - mIODist;
		
		float inerLength = (right - left) / 3;
		float [] points = {
				left, top, left + inerLength, top, 
				left, top, left, top + inerLength, 
				right - inerLength, top, right, top, 
				right, top, right, top + inerLength, 
				left, bottom - inerLength, left, bottom, 
				left, bottom, left + inerLength, bottom,
				right - inerLength, bottom, right, bottom, 
				right, bottom - inerLength, right, bottom,
		};
		canvas.drawLines(points, paint);
	}
	
	
	private float getStrokeWidth(final Bitmap bm, ImageView iv, float real) {
		int ivWidth = iv.getWidth();
		int ivHeight = iv.getHeight();
		int bmWidth = bm.getWidth();
		int bmHeight = bm.getHeight();
		if(ivWidth > bmWidth && ivHeight > bmHeight) {
			return real;
		}
		float wScale = (float)bmWidth / ivWidth;
		float hScale = (float) bmHeight / ivHeight;
		float scale = wScale < hScale ? wScale : hScale;
		return real * scale;
	}
	
	/**
	 * 计算Bitmap跟显示在ImageView中的倍数
	 * @param bm
	 * @param iv
	 * @return
	 */
	private float getScale(final Bitmap bm, final ImageView iv) {
		int ivWidth = iv.getWidth();
		int ivHeight = iv.getHeight();
		int bmWidth = bm.getWidth();
		int bmHeight = bm.getHeight();
		if(ivWidth > bmWidth && ivHeight > bmHeight) {
			return 1f;
		}
		float wScale = (float)bmWidth / ivWidth;
		float hScale = (float) bmHeight / ivHeight;
		return wScale < hScale ? wScale : hScale;
	}
}
