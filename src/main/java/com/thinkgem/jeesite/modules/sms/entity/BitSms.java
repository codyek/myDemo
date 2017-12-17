/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sms.entity;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 短信发送Entity
 * @author hzf
 * @version 2017-12-17
 */
public class BitSms extends DataEntity<BitSms> {
	
	private static final long serialVersionUID = 1L;
	private String useId;		// 用户id
	private String mobile;		// 发送号码
	private String content;		// 内容
	private String sendFlag;		// 是否发送成功：1 是， 0 否
	
	public BitSms() {
		super();
	}

	public BitSms(String id){
		super(id);
	}

	@Length(min=1, max=100, message="用户id长度必须介于 1 和 100 之间")
	public String getUseId() {
		return useId;
	}

	public void setUseId(String useId) {
		this.useId = useId;
	}
	
	@Length(min=1, max=100, message="发送号码长度必须介于 1 和 100 之间")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Length(min=1, max=100, message="内容长度必须介于 1 和 100 之间")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Length(min=1, max=2, message="是否发送成功：1 是， 0 否长度必须介于 1 和 2 之间")
	public String getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(String sendFlag) {
		this.sendFlag = sendFlag;
	}
	
}