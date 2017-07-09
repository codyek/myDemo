package com.thinkgem.jeesite.modules.platform.entity.apiauthor;

import org.hibernate.validator.constraints.Length;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * API授权Entity
 * @author hzf
 * @version 2017-07-08
 */
public class APIAuthorize extends DataEntity<APIAuthorize> {
	
	private static final long serialVersionUID = 1L;
	private String platform;		// 平台
	private User user;		// 用户id
	private String appkey;		// appkey
	private String secretkey;		// secretkey
	
	public APIAuthorize() {
		super();
	}

	public APIAuthorize(String id){
		super(id);
	}

	@Length(min=1, max=100, message="平台长度必须介于 1 和 100 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=120, message="appkey长度必须介于 1 和 120 之间")
	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	
	@Length(min=1, max=120, message="secretkey长度必须介于 1 和 120 之间")
	public String getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}
	
}