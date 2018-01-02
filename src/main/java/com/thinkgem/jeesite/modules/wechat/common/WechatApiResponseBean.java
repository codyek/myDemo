package com.thinkgem.jeesite.modules.wechat.common;

/**
 * 	微信API调用后，返回结果的bean
 * @author WernGin jhhuanga@isoftstone.com
 *
 */
public class WechatApiResponseBean {
	private String status="error";
	private Long code=-100L;
	private String errorMessage;
	private Object returnBody;
	
	/**
	 * status： 只有两个值， ok和error。默认值为 'error'
	 */
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * 对应微信的code表。
	 * 	其中，-100为系统定义的错误code，代表系统错误。默认值为-100
	 * @URL <a href="http://mp.weixin.qq.com/wiki/17/fa4e1434e57290788bde25603fa2fcbd.html">全局返回码说明</a>
	 */
	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	/**
	 * errorMessage：出错消息提示
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * 设置返回信息为成功！
	 */
	public void setSuccess() {
		this.setSuccess(WechatApiContants.RESP_STATUS_OK, WechatApiContants.RESP_CODE_OK);
	}
	
	/**
	 * 设置返回信息为成功！
	 */
	public void setSuccess(String status,Long code) {
		this.status = status;
		this.code = code;
		this.errorMessage = "";
	}
	/**
	 * 获取微信返回的消息体
	 * @return
	 */
	public Object getReturnBody() {
		return returnBody;
	}
	public void setReturnBody(Object returnBody) {
		this.returnBody = returnBody;
	}
}
