/*
 * 文件名：CreatePersonActivity.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-5-20
 * 修改人：xiaoying
 * 修改时间：2013-5-20
 * 版本：v1.0
 */
package com.xiaoying.facedemo.person;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaoying.facedemo.MainApplication;
import com.xiaoying.facedemo.R;
import com.xiaoying.facedemo.utils.LogUtil;
import com.xiaoying.facedemo.widget.TitleBar;
import com.xiaoying.faceplusplus.api.config.RespConfig;
import com.xiaoying.faceplusplus.api.entity.Person;
import com.xiaoying.faceplusplus.api.entity.request.person.PersonCreateReq;
import com.xiaoying.faceplusplus.api.entity.request.person.PersonSetInfoReq;
import com.xiaoying.faceplusplus.api.entity.response.person.PersonCreateResp;
import com.xiaoying.faceplusplus.api.entity.response.person.PersonSetInfoResp;
import com.xiaoying.faceplusplus.api.service.PersonService;

/**
 * 功能：创建Person
 * @author xiaoying
 */
public class CreatePersonActivity extends Activity {

	public static final String EXTRA_NEW_PERSON = "new_person";
	
	public static final String EXTRA_OLD_PERSON = "old_person";
	
	public static final String EXTRA_MODE = "mode";
	
	public static final int MODE_CREATE = 1;
	
	public static final int MODE_MODIFY = 2;
	
	private String tag = CreatePersonActivity.class.getSimpleName();
	
	private TitleBar mTitleBar = null;
	
	private ProgressDialog mProgressDialog = null;
	
	private EditText mEtName = null;
	
	private EditText mEtTag = null;
	
	private int mPosition = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_person);
		
		Intent intent = getIntent();
		
		if(intent.hasExtra(EXTRA_MODE)) {
			int mode = intent.getIntExtra(EXTRA_MODE, MODE_CREATE);
			initView(mode);
		} else {
			Toast.makeText(this, R.string.sys_err, Toast.LENGTH_SHORT).show();
			finish();
		}
		
		initProgressDialog();
	}
	
	private void initView(final int mode) {
		mTitleBar = (TitleBar) findViewById(R.id.tb_create_person);
		mEtName = (EditText) findViewById(R.id.et_create_person_name);
		mEtTag = (EditText) findViewById(R.id.et_create_person_tag);
		if(mode == MODE_CREATE) {
			mTitleBar.setTitle(R.string.create_pserson);
		} else if(mode == MODE_MODIFY) {
			mTitleBar.setTitle(R.string.modify_person);
			Intent intent = getIntent();
			if(intent.hasExtra(EXTRA_OLD_PERSON)) {
				mPosition = intent.getIntExtra(PersonListActivity.EXTRA_POSITION, -1);
				Person person = (Person) intent.getSerializableExtra(EXTRA_OLD_PERSON);
				mEtName.setText(person.getPerson_name());
				mEtTag.setText(person.getTag());
			} else {
				Toast.makeText(this, R.string.sys_err, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		mTitleBar.setLeftButton(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		mTitleBar.setRightButton(R.string.submit, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submit(mode);
			}
		});
	}
	
	private void submit(int mode) {
		if(mode == MODE_CREATE) {
			Person person = new Person();
			person.setPerson_name(mEtName.getText().toString());
			person.setTag(mEtTag.getText().toString());
			new CreatePerson().execute(person);
		} else if(mode == MODE_MODIFY) {
			Person person = (Person) getIntent().getSerializableExtra(EXTRA_OLD_PERSON);
			person.setPerson_name(mEtName.getText().toString());
			person.setTag(mEtTag.getText().toString());
			new ModifyPerson().execute(person);
		}
	}
	
	private void cancel() {
		setResult(RESULT_CANCELED);
		finish();
	}
	
	private void initProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
	}
	
	private void showProgressDialog(CharSequence message) {
		if(mProgressDialog == null) {
			initProgressDialog();
		}
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
	}
	
	private void dismissProgressDialog() {
		if(mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			cancel();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 新建一个Person
	 * @author xiaoying
	 *
	 */
	private class CreatePerson extends AsyncTask<Person, Void, PersonCreateResp> {

		private PersonService mmService = new PersonService(MainApplication.CLIENT);
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.submiting_data));
		}
		
		@Override
		protected PersonCreateResp doInBackground(Person... params) {
			Person person = params[0];
			try {
				PersonCreateReq req = new PersonCreateReq(person.getPerson_name());
				req.setTag(person.getTag());
				return mmService.createPerson(req);
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
		protected void onPostExecute(PersonCreateResp result) {
			super.onPostExecute(result);
			if(result != null) {
				if(result.getError_code() == RespConfig.RESP_OK) {
					LogUtil.w(tag, result);
					Person person = new Person();
					person.setPerson_id(result.getPerson_id());
					person.setPerson_name(result.getPerson_name());
					person.setTag(result.getTag());
					Intent data = new Intent();
					data.putExtra(EXTRA_NEW_PERSON, person);
					setResult(RESULT_OK, data);
					finish();
				} else {
					Toast.makeText(CreatePersonActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(CreatePersonActivity.this, R.string.net_err, Toast.LENGTH_SHORT).show();
			}
			dismissProgressDialog();
		}
		
	}
	
	/**
	 * 功能：修改Person信息
	 * @author xiaoying
	 *
	 */
	private class ModifyPerson extends AsyncTask<Person, Void, PersonSetInfoResp> {
		
		private PersonService mmService = new PersonService(MainApplication.CLIENT);
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.submiting_data));
		}
		
		@Override
		protected PersonSetInfoResp doInBackground(Person... params) {
			Person person = params[0];
			try {
				PersonSetInfoReq req = new PersonSetInfoReq(person.getPerson_id(), true);
				req.setName(person.getPerson_name());
				req.setTag(person.getTag());
				return mmService.setPersonInfo(req);
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
		protected void onPostExecute(PersonSetInfoResp result) {
			super.onPostExecute(result);
			if(result != null) {
				if(result.getError_code() == RespConfig.RESP_OK) {
					LogUtil.w(tag, result);
					Person person = new Person();
					person.setPerson_id(result.getPerson_id());
					person.setPerson_name(result.getPerson_name());
					person.setTag(result.getTag());
					Intent data = new Intent();
					data.putExtra(EXTRA_NEW_PERSON, person);
					data.putExtra(PersonListActivity.EXTRA_POSITION, mPosition);
					setResult(RESULT_OK, data);
					finish();
				} else {
					Toast.makeText(CreatePersonActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(CreatePersonActivity.this, R.string.net_err, Toast.LENGTH_SHORT).show();
			}
			dismissProgressDialog();
		}
	}
}
