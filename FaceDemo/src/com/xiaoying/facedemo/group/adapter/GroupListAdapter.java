/*
 * 文件名：GroupListAdapter.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-5-20
 * 修改人：xiaoying
 * 修改时间：2013-5-20
 * 版本：v1.0
 */
package com.xiaoying.facedemo.group.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoying.facedemo.R;
import com.xiaoying.faceplusplus.api.entity.Group;

/**
 * 功能：Group列表适配器
 * @author xiaoying
 */
public class GroupListAdapter extends BaseAdapter {
	
	private Context mContext = null;
	
	private List<Group> mGroups = new ArrayList<Group>();
	
	public GroupListAdapter(Context context) {
		this.mContext = context;
	}
	
	public void add(Group group) {
		mGroups.add(group);
		notifyDataSetChanged();
	}
	
	public void addAll(List<Group> groups) {
		mGroups.addAll(groups);
		notifyDataSetChanged();
	}
	
	public void remove(int position) {
		mGroups.remove(position);
		notifyDataSetChanged();
	}
	
	public void remove(Object object) {
		mGroups.remove(object);
		notifyDataSetChanged();
	}
	
	public void clear() {
		mGroups.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mGroups.size();
	}

	@Override
	public Group getItem(int position) {
		return mGroups.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHoder viewHoder = null;
		if(convertView == null) {
			viewHoder = new ViewHoder();
			convertView = View.inflate(mContext, R.layout.item_list, null);
			viewHoder.icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
			viewHoder.text1 = (TextView) convertView.findViewById(R.id.tv_item_text1);
			viewHoder.text2 = (TextView) convertView.findViewById(R.id.tv_item_text2);
			viewHoder.text3 = (TextView) convertView.findViewById(R.id.tv_item_text3);
			convertView.setTag(viewHoder);
		} else {
			viewHoder = (ViewHoder) convertView.getTag();
		}
		Group group = getItem(position);
		viewHoder.icon.setImageResource(R.drawable.ic_launcher);
		viewHoder.text1.setText(group.getGroup_name());
		viewHoder.text2.setText(group.getGroup_id());
		viewHoder.text3.setText(group.getTag());
		return convertView;
	}

	private static class ViewHoder {
		ImageView icon;
		TextView text1;
		TextView text2;
		TextView text3;
	}
}
