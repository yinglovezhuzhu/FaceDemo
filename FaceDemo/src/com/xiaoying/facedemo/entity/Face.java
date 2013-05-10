/* 文件名：Face.java
 * 作者：张运迎
 * 版本：v1.0
 * 修改日期：2013-05-09
 * 
 */
package com.xiaoying.facedemo.entity;

import android.graphics.PointF;
/**
 * 人脸实体类
 * @author xiaoying
 *
 */
public class Face {
//	face	array	被检测出的人脸的列表
	private float width; //0~100之间的实数，表示检出的脸的宽度在图片中百分比
	private float height; //0~100之间的实数，表示检出的脸的高度在图片中百分比
	private PointF center; 	//检出的人脸框的中心点坐标, x & y 坐标分别表示在图片中的宽度和高度的百分比 (0~100之间的实数)
	private PointF eye_left;	//相应人脸的左眼坐标，x & y 坐标分别表示在图片中的宽度和高度的百分比 (0~100之间的实数)
	private PointF eye_right;	//相应人脸的右眼坐标，x & y 坐标分别表示在图片中的宽度和高度的百分比 (0~100之间的实数)
	private PointF mouth_left;	//相应人脸的左侧嘴角坐标，x & y 坐标分别表示在图片中的宽度和高度的百分比 (0~100之间的实数)
	private PointF mouth_right;	//相应人脸的右侧嘴角坐标，x & y 坐标分别表示在图片中的宽度和高度的百分比 (0~100之间的实数)
	private PointF nose;	//相应人脸的鼻尖坐标，x & y 坐标分别表示在图片中的宽度和高度的百分比 (0~100之间的实数)
	
	/**
	 * 
	 * @author xiaoying
	 *
	 */
	public static class Attribute {
//	attribute	object	包含一系列人脸的属性分析结果
//	gender	object	包含性别分析结果，value的值为Male/Female, confidence表示置信度
		private Gender gender;
//	age	object	包含年龄分析结果，value的值为一个非负整数表示估计的年龄, range表示估计年龄的正负区间
		private Age age;
//	race	object	包含人种分析结果，value的值为Asian/White/Black, confidence表示置信度
		private Race race;
	}
	
	public static class Gender {
//		gender	object	包含性别分析结果，value的值为Male/Female, confidence表示置信度
		private String value;
		private float confidence;
	}
	
	public static class Age {
//		age	object	包含年龄分析结果，value的值为一个非负整数表示估计的年龄, range表示估计年龄的正负区间
		private int value;
		private int range;
	}
	
	public static class Race {
//		race	object	包含人种分析结果，value的值为Asian/White/Black, confidence表示置信度
		private String value;
		private float confidence;
	}
}
