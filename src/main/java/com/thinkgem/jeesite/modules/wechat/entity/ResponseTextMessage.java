package com.thinkgem.jeesite.modules.wechat.entity;

import com.thinkgem.jeesite.modules.wechat.common.WechatApiContants;


/**
 * 文本响应对象
 * 
 * @author Ly
 * 
 */
public class ResponseTextMessage extends IResponseMessage {

	private String content; // 响应文本

	private static final int MAX_CONTENT_LENGTH = 2048;// 微信文本消息最多支持2048字节

	public String getContent() {
		return content;
	}

	public ResponseTextMessage() {
		this.setMsgType("text");
	}

	
	public ResponseTextMessage(String toUserName) {
		super(toUserName, WechatApiContants.WECHATID);
		this.setMsgType("text");
	}
	public ResponseTextMessage(String toUserName, String content) {
		super(toUserName, WechatApiContants.WECHATID);
		this.setMsgType("text");
		this.setContent(content);
	}

	public ResponseTextMessage(String toUserName, String fromUserName, String content) {
		this(toUserName, fromUserName);
		this.setContent(content);
	}

	public void setContent(String content) {
		// 设置最大限制字符长度
		if (content != null && content.getBytes().length > MAX_CONTENT_LENGTH) {
			content = content.substring(0, MAX_CONTENT_LENGTH / 2 - 3) + "...";
		}
		this.content = content;
	}

	public String toMessageString() {
		StringBuffer buffer = new StringBuffer("<xml>");
		buffer.append("<ToUserName><![CDATA[").append(getToUserName())
				.append("]]></ToUserName>");
		buffer.append("<FromUserName><![CDATA[").append(getFromUserName())
				.append("]]></FromUserName>");
		buffer.append("<CreateTime>").append(System.currentTimeMillis())
				.append("</CreateTime>");
		buffer.append("<MsgType><![CDATA[" + getMsgType() + "]]></MsgType>");
		buffer.append("<Content><![CDATA[")
				.append(content == null ? "" : content
						.replaceAll("\\\\n", "\n")).append("]]></Content>");
		buffer.append("</xml>");
		return buffer.toString();
	}

	@Override
	public String toCustJsonString() {
		StringBuffer buffer = new StringBuffer();
		String customContent = getContent() ; 
		if(customContent.indexOf("\"") >= 0 ){
			customContent = customContent.replaceAll("\"", "\\\\\"");
		}
		buffer.append("{\"touser\":\"")
				.append(this.getToUserName())
				.append("\",\"msgtype\":\"text\",\"text\":{\"content\":\"")
				.append(customContent == null ? "" : customContent
						.replaceAll("\\\\n", "\n")).append("\"}}");
		return buffer.toString();
	}
	
	public String toTemplateJsonString() {
		StringBuffer buffer = new StringBuffer();
		String customContent = getContent() ; 
		if(customContent.indexOf("\"") >= 0 ){
			customContent = customContent.replaceAll("\"", "\\\\\"");
		}
		buffer.append("{\"touser\":\"")
				.append(this.getToUserName())
				.append("\",\"template_id\":\"erKKX1NVATsQ-nmqVPh7mubqrgpS69sQY_ST9KCN3OQ\",\"data\":\"")
				.append(customContent == null ? "" : customContent
						.replaceAll("\\\\n", "\n")).append("\"}");
		return buffer.toString();
	}
}
