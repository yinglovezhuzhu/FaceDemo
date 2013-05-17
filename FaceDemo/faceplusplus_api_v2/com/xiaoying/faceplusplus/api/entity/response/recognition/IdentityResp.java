/*
 * 文件名：IdentityResp.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-5-15
 * 修改人：xiaoying
 * 修改时间：2013-5-15
 * 版本：v1.0
 */

package com.xiaoying.faceplusplus.api.entity.response.recognition;

import java.util.List;

import com.xiaoying.faceplusplus.api.entity.Face;
import com.xiaoying.faceplusplus.api.entity.response.BaseResponse;

/**
 * 功能：对于一个待查询的Face列表（或者对于给定的Image中所有的Face），在一个Group中查询最相似的Person的请求返回实体类
 * @author xiaoying
 *
 */
public class IdentityResp extends BaseResponse {

	private String session_id;	//相应请求的session标识符，可用于结果查询
	private List<IdentityFace> face; //人脸的列表
	
	/**
	 * 相应请求的session标识符，可用于结果查询
	 * @return the session_id
	 */
	public String getSession_id() {
		return session_id;
	}

	/**
	 * 相应请求的session标识符，可用于结果查询
	 * @param session_id the session_id to set
	 */
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	/**
	 * 相应请求的session标识符，可用于结果查询
	 * @return the face
	 */
	public List<IdentityFace> getFace() {
		return face;
	}

	/**
	 * 相应请求的session标识符，可用于结果查询
	 * @param face the face to set
	 */
	public void setFace(List<IdentityFace> face) {
		this.face = face;
	}
	
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IdentityResp [session_id=" + session_id + ", face=" + face
				+ ", error=" + error + ", error_code=" + error_code + "]";
	}




	/**
	 * 功能：人脸识别返回的人脸信息实体类
	 * @author xiaoying
	 *
	 */
	public static class IdentityFace {
		private String face_id;	//识别匹配的face_id
		private Face.Position position;	//识别匹配的face_id相关的位置信息
		private List<Candidate> candidates;	//识别结果。candidates包含不超过3个人，包含相应person信息与相应的置信度
		/**
		 * 识别匹配的face_id
		 * @return the face_id
		 */
		public String getFace_id() {
			return face_id;
		}
		/**
		 * 识别匹配的face_id
		 * @param face_id the face_id to set
		 */
		public void setFace_id(String face_id) {
			this.face_id = face_id;
		}
		/**
		 * 识别匹配的face_id相关的位置信息
		 * @return the position
		 */
		public Face.Position getPosition() {
			return position;
		}
		/**
		 * 识别匹配的face_id相关的位置信息
		 * @param position the position to set
		 */
		public void setPosition(Face.Position position) {
			this.position = position;
		}
		/**
		 * 识别结果。candidates包含不超过3个人，包含相应person信息与相应的置信度
		 * @return the candidates
		 */
		public List<Candidate> getCandidates() {
			return candidates;
		}
		/**
		 * 识别结果。candidates包含不超过3个人，包含相应person信息与相应的置信度
		 * @param candidates the candidates to set
		 */
		public void setCandidates(List<Candidate> candidates) {
			this.candidates = candidates;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "IdentityFace [face_id=" + face_id + ", position="
					+ position + ", candidates=" + candidates + "]";
		}
	}
	
	/**
	 * 功能：识别结果实体类
	 * @author xiaoying
	 *
	 */
	public static class Candidate {
		private String person_id;	//person的id
		private String person_name;	//person的name
		private String tag;	//person的tag
		private float confidence;	//匹配的置信度
		/**
		 * person的id
		 * @return the person_id
		 */
		public String getPerson_id() {
			return person_id;
		}
		/**
		 * person的id
		 * @param person_id the person_id to set
		 */
		public void setPerson_id(String person_id) {
			this.person_id = person_id;
		}
		/**
		 * person的name
		 * @return the person_name
		 */
		public String getPerson_name() {
			return person_name;
		}
		/**
		 * person的name
		 * @param person_name the person_name to set
		 */
		public void setPerson_name(String person_name) {
			this.person_name = person_name;
		}
		/**
		 * person的tag
		 * @return the tag
		 */
		public String getTag() {
			return tag;
		}
		/**
		 * person的tag
		 * @param tag the tag to set
		 */
		public void setTag(String tag) {
			this.tag = tag;
		}
		/**
		 * 匹配的置信度
		 * @return the confidence
		 */
		public float getConfidence() {
			return confidence;
		}
		/**
		 * 匹配的置信度
		 * @param confidence the confidence to set
		 */
		public void setConfidence(float confidence) {
			this.confidence = confidence;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Candidate [person_id=" + person_id + ", person_name="
					+ person_name + ", tag=" + tag + ", confidence="
					+ confidence + "]";
		}
	}
}
