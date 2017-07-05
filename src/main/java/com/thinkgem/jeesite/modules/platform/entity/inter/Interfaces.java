/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.entity.inter;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 各平台接口信息Entity
 * @author hzf
 * @version 2017-07-06
 */
public class Interfaces extends DataEntity<Interfaces> {
	
	private static final long serialVersionUID = 1L;
	private String platform;		// 平台
	private String type;		// 类型
	private String code;		// 编码
	private String url;		// 接口地址
	private String describe;		// 接口描述
	
	public Interfaces() {
		super();
	}

	public Interfaces(String id){
		super(id);
	}

	@Length(min=1, max=100, message="平台长度必须介于 1 和 100 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=1, max=100, message="类型长度必须介于 1 和 100 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=1, max=100, message="编码长度必须介于 1 和 100 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Length(min=1, max=100, message="接口地址长度必须介于 1 和 100 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Length(min=0, max=100, message="接口描述长度必须介于 0 和 100 之间")
	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
}