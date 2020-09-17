package com.kedacom.tz.sh.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 会议混音
 * 
 * @author ysl
 *
 */
@Data
public class MixOperateModel {

	/** 会议成员唯一标识集合 **/
	private List<Member> members;

	/** 混音模式 1-智能混音； 2-定制混音； **/
	private Integer mode;

	@Data
	private class Member {
		/** (必填) 终端号 最大字符长度：48个字节 **/
		private String mt_id;
	}

	public void addMember(String mtId) {
		if (this.members == null) {
			this.members = new ArrayList<>();
		}
		Member mt = new Member();
		mt.setMt_id(mtId);
		this.members.add(mt);
	}

}
