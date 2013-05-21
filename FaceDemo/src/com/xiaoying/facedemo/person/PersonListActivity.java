/*
 * 文件名：PersonListActivity.java
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
import java.io.Serializable;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.xiaoying.facedemo.MainApplication;
import com.xiaoying.facedemo.R;
import com.xiaoying.facedemo.person.adapter.PersonListAdapter;
import com.xiaoying.facedemo.utils.LogUtil;
import com.xiaoying.facedemo.widget.TitleBar;
import com.xiaoying.faceplusplus.api.config.RespConfig;
import com.xiaoying.faceplusplus.api.entity.Person;
import com.xiaoying.faceplusplus.api.entity.request.group.GroupCreateReq;
import com.xiaoying.faceplusplus.api.entity.response.group.GroupCreateResp;
import com.xiaoying.faceplusplus.api.entity.response.info.InfoGetPersonListResp;
import com.xiaoying.faceplusplus.api.service.GroupService;
import com.xiaoying.faceplusplus.api.service.InfoService;

/**
 * 功能：Person列表
 * @author xiaoying
 */
public class PersonListActivity extends Activity {

	public static final int MODE_VIEW = 1;
	
	public static final int MODE_PICK = 2;
	
	public static final int MODE_CHOOSE = 3;
	
	public static final String EXTRA_MODE = "mode";
	
	public static final String EXTRA_PERSON = "person";
	
	public static final String EXTRA_PERSON_ARRAY = "person_array";
	
	private String tag = PersonListActivity.class.getSimpleName();
	
	private TitleBar mTitleBar = null;
	
	private ListView mListView = null;
	
	private PersonListAdapter mAdapter = null;

	private ProgressDialog mProgressDialog = null;
	
	private int mMode = MODE_VIEW;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_list);
	
		mMode = getIntent().getIntExtra(EXTRA_MODE, MODE_VIEW);
		
		initView();
		
		new GetPersons().execute();
	}
	
	private void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_person_list_title);
		mListView = (ListView) findViewById(R.id.lv_person_list);
		
		mTitleBar.setLeftButton(R.string.backe, mLeftClick);
		if(mMode == MODE_VIEW) {
			mTitleBar.setTitle(R.string.person_manager);
			mTitleBar.setRightButton(R.string.create, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			mAdapter = new PersonListAdapter(this);
			mListView.setOnItemClickListener(mViewItemClick);
		} else if(mMode == MODE_PICK) {
			mTitleBar.setTitle(R.string.choose_person);
			mTitleBar.setRightButtonVisible(false);
			mAdapter = new PersonListAdapter(this, MODE_PICK);
			mListView.setOnItemClickListener(mPicItemClick);
		} else if(mMode == MODE_CHOOSE) {
			mTitleBar.setTitle(R.string.choose_person);
			mTitleBar.setRightButton(R.string.ok, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					LogUtil.e(tag, mAdapter.getCheckedPerson());
					Intent data = new Intent();
					data.putExtra("", (Serializable) mAdapter.getCheckedPerson());
					setResult(RESULT_OK, data);
				}
			});
			mAdapter = new PersonListAdapter(this, MODE_CHOOSE);
			mListView.setOnItemClickListener(mChooseItemClick);
		}
		
		mListView.setAdapter(mAdapter);
		
		initProgressDialog();
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
	
	private View.OnClickListener mLeftClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (mMode) {
				case MODE_PICK:
					setResult(RESULT_CANCELED);
					break;
				case MODE_CHOOSE:
					setResult(RESULT_CANCELED);
				default :
					break;
			}
			finish();
		}
	};
	
	private AdapterView.OnItemClickListener mViewItemClick = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
		}
	};
	
	private AdapterView.OnItemClickListener mPicItemClick = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			LogUtil.e(tag, mAdapter.getItem(position));
			Intent data = new Intent();
			data.putExtra(EXTRA_PERSON, mAdapter.getItemId(position));
			setResult(RESULT_OK, data);
		}
	};
	
	private AdapterView.OnItemClickListener mChooseItemClick = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mAdapter.setChecked(position, !mAdapter.isChecked(position));
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private class GetPersons extends AsyncTask<Void, Void, InfoGetPersonListResp> {

		private InfoService mmService = new InfoService(MainApplication.CLIENT);
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.msg_getting_data));
		}
		
		@Override
		protected InfoGetPersonListResp doInBackground(Void... params) {
			
			try {
				return mmService.getPersonList();
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
		protected void onPostExecute(InfoGetPersonListResp result) {
			super.onPostExecute(result);
			if(result != null) {
				if(result.getError_code() == RespConfig.RESP_OK) {
					List<Person> persons = result.getPerson();
					if(persons != null && !persons.isEmpty()) {
						mAdapter.addAll(persons);
					} else {
						Toast.makeText(PersonListActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(PersonListActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
				dismissProgressDialog();
			}
		}
		
	}

	private void createPerson(String personName, String tag) {
		GroupCreateReq req = new GroupCreateReq(personName);
		req.setTag(tag);
		new CreatePerson().execute(req);
	}
	
	private class CreatePerson extends AsyncTask<GroupCreateReq, Void, GroupCreateResp> {

		private GroupService mmService = new GroupService(MainApplication.CLIENT);
		@Override
		protected GroupCreateResp doInBackground(GroupCreateReq... params) {
			try {
				return mmService.createGroup(params[0]);
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
		protected void onPostExecute(GroupCreateResp result) {
			super.onPostExecute(result);
			if(result != null) {
				if(result.getError_code() == RespConfig.RESP_OK) {
					LogUtil.w(tag, result);
					Person person = new Person();
					person.setPerson_id(result.getGroup_id());
					person.setPerson_name(result.getGroup_name());
					person.setTag(result.getTag());
					mAdapter.add(person);
				} else {
					Toast.makeText(PersonListActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			}
			dismissProgressDialog();
		}
	}
}
