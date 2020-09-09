package com.kedacom.tz.sh.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 
 * 批量删除/呼叫/挂断终端
 * 
 * @author ysl
 *
 */
@Data
public class OperateMtsModel {

	private List<OperateMt> mts;

	@Data
	public class OperateMt {

		/** (必填) 终端号 最大字符长度：48个字节 **/
		private String mt_id;

		/** 是否强制呼叫，默认是0 0-不强呼； 1-强呼； **/
		private Integer forced_call;
	}

	public void addMt(String mtId) {
		if (this.mts == null) {
			this.mts = new ArrayList<>();
		}
		OperateMt mt = new OperateMt();
		mt.setMt_id(mtId);
		this.mts.add(mt);
	}
}
