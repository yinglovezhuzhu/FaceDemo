/*
 * 文件名：PointF.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-5-10
 * 修改人：xiaoying
 * 修改时间：2013-5-10
 * 版本：v1.0
 */
package com.xiaoying.faceplusplus.api.entity;
/**
 * 功能：浮点型的点
 */
public class PointF {
	public float x = 0;
	public float y = 0;
	
	public PointF() {
		
	}
	
	public PointF(PointF point) {
		this.x = point.x;
		this.y = point.y;
	}
	
	public PointF(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(PointF point) {
		this.x = point.x;
		this.y = point.y;
	}

	@Override
	public String toString() {
		return "PointF [x=" + x + ", y=" + y + "]";
	}
}
