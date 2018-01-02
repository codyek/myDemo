package com.thinkgem.jeesite.modules.wechat.entity;

/**
 * 用户响应对象
 * 
 * @author ly
 * 
 * */
public abstract class IResponseMessage {

	/**
	 * 接收方帐号（收到的OpenID）
	 * */
	private String toUserName;

	/**
	 * 开发者微信号
	 * */
	private String fromUserName;

	/**
	 * 消息类型
	 * */
	private String msgType;

	/**
	 * 构造响应XML文本
	 * 
	 * */

	public abstract String toMessageString();
	
	/**
	 * 构造客服接口响应json
	 * @return
	 */
	public abstract String toCustJsonString();
	
	public IResponseMessage() {}

	public IResponseMessage(String toUserName, String fromUserName) {
		this.setFromUserName(fromUserName);
		this.setToUserName(toUserName);
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
}
