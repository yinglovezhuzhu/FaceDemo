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

import com.xiaoying.facedemo.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * 功能：Person列表
 * @author xiaoying
 */
public class PersonListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_list);
	}
}
