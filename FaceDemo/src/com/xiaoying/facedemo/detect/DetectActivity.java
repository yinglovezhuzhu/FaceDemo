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
import java.util.ArrayList;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.annotation.SuppressLint;
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
import com.xiaoying.facedemo.db.util.FaceDBUtil;
import com.xiaoying.facedemo.db.util.FaceFacesetDBUtil;
import com.xiaoying.facedemo.db.util.FacesetDBUtil;
import com.xiaoying.facedemo.db.util.ImageDBUtil;
import com.xiaoying.facedemo.utils.BitmapUtil;
import com.xiaoying.facedemo.utils.DateUtil;
import com.xiaoying.facedemo.utils.FileUtil;
import com.xiaoying.facedemo.utils.LogUtil;
import com.xiaoying.facedemo.widget.MarkFaceView;
import com.xiaoying.facedemo.widget.TitleBar;
import com.xiaoying.faceplusplus.api.cliet.Client;
import com.xiaoying.faceplusplus.api.config.RespConfig;
import com.xiaoying.faceplusplus.api.entity.Face;
import com.xiaoying.faceplusplus.api.entity.Faceset;
import com.xiaoying.faceplusplus.api.entity.Image;
import com.xiaoying.faceplusplus.api.entity.request.face.DetectReq;
import com.xiaoying.faceplusplus.api.entity.request.faceset.FacesetAddFaceReq;
import com.xiaoying.faceplusplus.api.entity.request.faceset.FacesetCreateReq;
import com.xiaoying.faceplusplus.api.entity.response.face.DetectResp;
import com.xiaoying.faceplusplus.api.entity.response.faceset.FacesetAddFaceResp;
import com.xiaoying.faceplusplus.api.entity.response.faceset.FacesetCreateResp;
import com.xiaoying.faceplusplus.api.service.FaceService;
import com.xiaoying.faceplusplus.api.service.FacesetService;

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
	
	private String mBitmapPath = null;
	
	private Faceset mFaceset = null;
	
	private Image mImage = new Image();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detect);
		
		initView();
		
		initData();
		
		DisplayMetrics m = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(m);
		
		mBitmap = BitmapUtil.loadBitmap(mBitmapPath, m.widthPixels, m.heightPixels);
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
					new UploadImage(MainApplication.CLIENT).execute(file);
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
		getFaceset();
	}
	
	
	private void getFaceset() {
		List<Faceset> facesets = FacesetDBUtil.getFacesetsByKeyName(this, MainApplication.USER_NAME);
		if(facesets.isEmpty()) {
			createNewFaceset();
		} else {
			for (Faceset faceset : facesets) {
				if(faceset.getFace_count() < RespConfig.FACESET_MAX_FACE) {
					mFaceset = faceset;
					break;
				}
			}
			if(mFaceset == null) {
				createNewFaceset();
			}
		}
	}
	
	/**
	 * 创建一个新的Faceset
	 * @return
	 */
	private void createNewFaceset() {
		FacesetCreateReq req = new FacesetCreateReq(createNewFacesetName());
		req.setTag("This is a faceset create by " + MainApplication.USER_NAME + " in " + DateUtil.getNowDate("yyyy-MM-dd HH:mm:ss"));
		new CreateFacset().execute(req);
	}
	
	/**
	 * 生成一个新的Faceset name
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String createNewFacesetName() {
		return  MainApplication.USER_NAME + "_" + DateUtil.getNowDate("yyyy-MM-dd_HHmmss");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 功能：上传人脸进行识别
	 * @author xiaoying
	 *
	 */
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
					mImage.setImageId(result.getImg_id());
					mImage.setImg(mBitmapPath);
					mImage.setUrl(result.getUrl());
					mImage.setWidth(result.getImg_width());
					mImage.setHeight(result.getImg_height());
					
					new AddFaceToFaceset(faces).execute();
				} else {
					Toast.makeText(DetectActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	
	/**
	 * 功能：创建一个新的Faceset
	 * @author xiaoying
	 *
	 */
	private class CreateFacset extends AsyncTask<FacesetCreateReq, Void, FacesetCreateResp> {

		private FacesetService mmService = null;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mmService = new FacesetService(MainApplication.CLIENT);
		}
		
		@Override
		protected FacesetCreateResp doInBackground(FacesetCreateReq... params) {
			try {
				return mmService.createFaceset(params[0]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(FacesetCreateResp result) {
			super.onPostExecute(result);
			if(result != null) {
				if(result.getError_code() == RespConfig.RESP_OK) {
					Faceset faceset = new Faceset();
					faceset.setFaceset_id(result.getFaceset_id());
					faceset.setFaceset_name(result.getFaceset_name());
					faceset.setTag(result.getTag());
					faceset.setFace_count(result.getAdded_face());
					FacesetDBUtil.insertFaceset(DetectActivity.this, faceset);
					mFaceset = faceset;
				} else {
					Toast.makeText(DetectActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			}
		}
		
	}
	
	/**
	 * 功能：添加Face到Faceset中
	 * @author xiaoying
	 *
	 */
	private class AddFaceToFaceset extends AsyncTask<Void, Void, FacesetAddFaceResp> {
		
		private List<Face> mmFaces = new ArrayList<Face>();
		private FacesetService mmService = new FacesetService(MainApplication.CLIENT);
		
		public AddFaceToFaceset(List<Face> faces) {
			if(faces == null || faces.isEmpty()) {
				throw new IllegalArgumentException("Faces should not empty or null");
			}
			mmFaces = faces;
		}

		@Override
		protected FacesetAddFaceResp doInBackground(Void... params) {
			FacesetAddFaceReq req = new FacesetAddFaceReq(mFaceset.getFaceset_id(), true);
			req.setFace_id(makeFaceIds(mmFaces));
			try {
				return mmService.addFace(req);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(FacesetAddFaceResp result) {
			super.onPostExecute(result);
			if(result != null) {
				if(result.getError_code() == RespConfig.RESP_OK) {
					if(result.getAdded() > 0) {
						mFaceset.setFace_count(mFaceset.getFace_count() + result.getAdded());
						FaceDBUtil.insertFaces(DetectActivity.this, mmFaces);
						FacesetDBUtil.setFaceCount(DetectActivity.this, mFaceset.getFaceset_id(), mFaceset.getFace_count());
						ImageDBUtil.insertImage(DetectActivity.this, mImage);
						FaceFacesetDBUtil.insertFaces(DetectActivity.this, mmFaces, mFaceset.getFaceset_id());
					}
				} else {
					Toast.makeText(DetectActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		private String makeFaceIds(List<Face> faces) {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < faces.size(); i++) {
				if(i > 0) {
					sb.append(",");
				}
				sb.append(faces.get(i).getFace_id());
			}
			return sb.toString();
		}
	}
}
