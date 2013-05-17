/*
 * 文件名：InfoGetFaceResp.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-5-14
 * 修改人：xiaoying
 * 修改时间：2013-5-14
 * 版本：v1.0
 */

package com.xiaoying.faceplusplus.api.entity.response.info;

import java.util.List;

import com.xiaoying.faceplusplus.api.entity.Face;
import com.xiaoying.faceplusplus.api.entity.Faceset;
import com.xiaoying.faceplusplus.api.entity.Person;
import com.xiaoying.faceplusplus.api.entity.response.BaseResponse;

/**
 * 功能：查询Face信息请求返回实体类
 * @author xiaoying
 *
 */
public class InfoGetFaceResp extends BaseResponse {

	private List<FaceInfo> face_info; //人脸相关信息
	
	/**
	 * 人脸相关信息
	 * @return the face_info
	 */
	public List<FaceInfo> getFace_info() {
		return face_info;
	}

	/**
	 * 人脸相关信息
	 * @param face_info the face_info to set
	 */
	public void setFace_info(List<FaceInfo> face_info) {
		this.face_info = face_info;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InfoGetFaceResp [face_info=" + face_info + ", error=" + error
				+ ", error_code=" + error_code + "]";
	}

	public static class FaceInfo {
		private String face_id;	//人脸ID
		private String img_id;	//图片id
		private String url;	//图片url
		private String tag;	//Face的tag
		private Face.Attribute attribute;	//人脸相关属性的信息
		private Face.Position position;		//人脸相关的位置信息数据
		private List<Person> person;	//包含该face的Person信息
		private List<Faceset> faceset;	//包含该face的faceset信息
		
		/**
		 * 人脸ID
		 * @return the face_id
		 */
		public String getFace_id() {
			return face_id;
		}
		/**
		 * 人脸ID
		 * @param face_id the face_id to set
		 */
		public void setFace_id(String face_id) {
			this.face_id = face_id;
		}
		/**
		 * 图片id
		 * @return the img_id
		 */
		public String getImg_id() {
			return img_id;
		}
		/**
		 * 图片id
		 * @param img_id the img_id to set
		 */
		public void setImg_id(String img_id) {
			this.img_id = img_id;
		}
		/**
		 * 图片url
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}
		/**
		 * 图片url
		 * @param url the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}
		/**
		 * Face的tag
		 * @return the tag
		 */
		public String getTag() {
			return tag;
		}
		/**
		 * Face的tag
		 * @param tag the tag to set
		 */
		public void setTag(String tag) {
			this.tag = tag;
		}
		/**
		 * 人脸相关属性的信息
		 * @return the attribute
		 */
		public Face.Attribute getAttribute() {
			return attribute;
		}
		/**
		 * 人脸相关属性的信息
		 * @param attribute the attribute to set
		 */
		public void setAttribute(Face.Attribute attribute) {
			this.attribute = attribute;
		}
		/**
		 * 人脸相关的位置信息数据
		 * @return the position
		 */
		public Face.Position getPosition() {
			return position;
		}
		/**
		 * 人脸相关的位置信息数据
		 * @param position the position to set
		 */
		public void setPosition(Face.Position position) {
			this.position = position;
		}
		/**
		 * 包含该face的Person信息
		 * @return the person
		 */
		public List<Person> getPerson() {
			return person;
		}
		/**
		 * 包含该face的Person信息
		 * @param person the person to set
		 */
		public void setPerson(List<Person> person) {
			this.person = person;
		}
		/**
		 * 包含该face的faceset信息
		 * @return the faceset
		 */
		public List<Faceset> getFaceset() {
			return faceset;
		}
		/**
		 * 包含该face的faceset信息
		 * @param faceset the faceset to set
		 */
		public void setFaceset(List<Faceset> faceset) {
			this.faceset = faceset;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "FaceInfo [face_id=" + face_id + ", img_id=" + img_id
					+ ", url=" + url + ", tag=" + tag + ", attribute="
					+ attribute + ", position=" + position + ", person="
					+ person + ", faceset=" + faceset + "]";
		}
	}
}
