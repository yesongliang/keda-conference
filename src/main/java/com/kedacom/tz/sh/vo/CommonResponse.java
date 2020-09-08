package com.kedacom.tz.sh.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "统一返回数据格式")
public class CommonResponse<T> implements Serializable {

	private static final long serialVersionUID = 5335534790538805512L;

	/** code=0,成功 **/
	@ApiModelProperty(value = "状态码")
	private Integer code;
	@ApiModelProperty(value = "消息")
	private String message;
	@ApiModelProperty(value = "数据")
	private T data;

	public CommonResponse(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

}
