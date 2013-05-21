/*
 * 文件名：GroupListActivity.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-5-20
 * 修改人：xiaoying
 * 修改时间：2013-5-20
 * 版本：v1.0
 */
package com.xiaoying.facedemo.group;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.xiaoying.facedemo.MainApplication;
import com.xiaoying.facedemo.R;
import com.xiaoying.facedemo.db.util.GroupDBUtil;
import com.xiaoying.facedemo.group.adapter.GroupListAdapter;
import com.xiaoying.facedemo.utils.DateUtil;
import com.xiaoying.facedemo.utils.LogUtil;
import com.xiaoying.facedemo.widget.TitleBar;
import com.xiaoying.faceplusplus.api.config.RespConfig;
import com.xiaoying.faceplusplus.api.entity.Group;
import com.xiaoying.faceplusplus.api.entity.request.group.GroupCreateReq;
import com.xiaoying.faceplusplus.api.entity.response.group.GroupCreateResp;
import com.xiaoying.faceplusplus.api.entity.response.info.InfoGetGroupListResp;
import com.xiaoying.faceplusplus.api.service.GroupService;
import com.xiaoying.faceplusplus.api.service.InfoService;

/**
 * 功能：Group列表
 * @author xiaoying
 */
public class GroupListActivity extends Activity {
	
	public static final int MODE_VIEW = 1;
	
	public static final int MODE_PICK = 2;
	
	public static final int MODE_CHOOSE = 3;
	
	public static final String EXTRA_MODE = "mode";
	
	private String tag = GroupListActivity.class.getSimpleName();
	
	private TitleBar mTitleBar = null;
	
	private ListView mListView = null;
	
	private GroupListAdapter mAdapter = null;

	private ProgressDialog mProgressDialog = null;
	
	private int mMode = MODE_VIEW;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_list);
		
		mMode = getIntent().getIntExtra(EXTRA_MODE, MODE_VIEW);
		
		initView();
		
		new GetGroups().execute();
	}
	
	private void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_group_list_title);
		
		mTitleBar.setLeftButton(R.string.backe, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if(mMode == MODE_VIEW) {
			mTitleBar.setTitle(R.string.group_manager);
			mTitleBar.setRightButton(R.string.create, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
		} else if(mMode == MODE_PICK) {
			mTitleBar.setTitle(R.string.choose_group);
			mTitleBar.setRightButtonVisible(false);
		} else if(mMode == MODE_CHOOSE) {
			mTitleBar.setTitle(R.string.choose_group);
			mTitleBar.setRightButton(R.string.ok, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		mListView = (ListView) findViewById(R.id.lv_group_list);
		mAdapter = new GroupListAdapter(this);
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private class GetGroups extends AsyncTask<Void, Void, InfoGetGroupListResp> {

		private InfoService mmService = new InfoService(MainApplication.CLIENT);
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.msg_getting_data));
		}
		
		@Override
		protected InfoGetGroupListResp doInBackground(Void... params) {
			
			try {
				return mmService.getGroupList();
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
		protected void onPostExecute(InfoGetGroupListResp result) {
			super.onPostExecute(result);
			if(result != null) {
				if(result.getError_code() == RespConfig.RESP_OK) {
					List<Group> groups = result.getGroup();
					if(groups == null) {
						createGroup();
					} else {
						if(groups.isEmpty()) {
							createGroup();
						} else {
							GroupDBUtil.updateOrInsertGroups(GroupListActivity.this, groups);
							mAdapter.addAll(groups);
							dismissProgressDialog();
						}
					}
				} else {
					dismissProgressDialog();
					Toast.makeText(GroupListActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		private void createGroup() {
			GroupCreateReq req = new GroupCreateReq(MainApplication.USER_NAME + "_Group_" + DateUtil.getNowDate("yyyy-MM-dd_HHmmss"));
			req.setTag("This is a faceset create by " + MainApplication.USER_NAME + " in " + DateUtil.getNowDate("yyyy-MM-dd HH:mm:ss"));
			new CreateGroup().execute(req);
		}
	}
	
	private class CreateGroup extends AsyncTask<GroupCreateReq, Void, GroupCreateResp> {

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
					Group group = new Group();
					group.setGroup_id(result.getGroup_id());
					group.setGroup_name(result.getGroup_name());
					group.setTag(result.getTag());
					GroupDBUtil.insertGroup(GroupListActivity.this, group);
					mAdapter.add(group);
				} else {
					Toast.makeText(GroupListActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
				}
			}
			dismissProgressDialog();
		}
	}
}
