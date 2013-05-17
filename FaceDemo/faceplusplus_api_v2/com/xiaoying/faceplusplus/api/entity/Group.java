/*
 * 文件名：Group.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-5-10
 * 修改人：xiaoying
 * 修改时间：2013-5-10
 * 版本：v1.0
 */
package com.xiaoying.faceplusplus.api.entity;

import java.util.List;

/**
 * 功能：Group实体类
 * @author xiaoying
 */
public class Group {
	private String group_id;	//相应group的id
	private String group_name;	//相应group的name
	private String tag;	//group相关的tag
	private List<Person> person;	//属于该group的person信息
	
	/**
	 * 相应group的id
	 * @return
	 */
	public String getGroup_id() {
		return group_id;
	}
	
	/**
	 * 相应group的id
	 * @param group_id
	 */
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	
	/**
	 * 相应group的name
	 * @return
	 */
	public String getGroup_name() {
		return group_name;
	}
	
	/**
	 * 相应group的name
	 * @param group_name
	 */
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	
	/**
	 * group相关的tag
	 * @return
	 */
	public String getTag() {
		return tag;
	}
	
	/**
	 * group相关的tag
	 * @param tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	/**
	 * 属于该group的person信息
	 * @return
	 */
	public List<Person> getPerson() {
		return person;
	}
	
	/**
	 * 属于该group的person信息
	 * @param person
	 */
	public void setPerson(List<Person> person) {
		this.person = person;
	}
	
	@Override
	public String toString() {
		return "Group [group_id=" + group_id + ", group_name=" + group_name
				+ ", tag=" + tag + ", person=" + person + "]";
	}
}
