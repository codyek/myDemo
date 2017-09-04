package com.thinkgem.jeesite.modules.platform.entity.trade;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 监控管理Entity
 * @author hzf
 * @version 2017-08-27
 */
public class BitMonitor extends DataEntity<BitMonitor> {
	
	private static final long serialVersionUID = 1L;
	private String code;		// 编码
	private Date openTime;		// 开始时间
	private Date closeTime;		// 结束时间
	private String statusFlag;		// 状态：1 运行中, 0 结束
	private User user;		// 用户id
	private String threadId;		// 线程id
	private String openOrderQty;		// 开单数量
	private String closeOrderQty;		// 平单数量
	private Date newTime;		// 监控最新运行时间
	
	public BitMonitor() {
		super();
	}

	public BitMonitor(String id){
		super(id);
	}

	@Length(min=1, max=100, message="编码长度必须介于 1 和 100 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}
	
	@Length(min=1, max=2, message="状态：1 运行中, 0 结束长度必须介于 1 和 2 之间")
	public String getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(String statusFlag) {
		this.statusFlag = statusFlag;
	}
	
	@NotNull(message="用户id不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=100, message="线程id长度必须介于 1 和 100 之间")
	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	
	@Length(min=0, max=6, message="开单数量长度必须介于 0 和 6 之间")
	public String getOpenOrderQty() {
		return openOrderQty;
	}

	public void setOpenOrderQty(String openOrderQty) {
		this.openOrderQty = openOrderQty;
	}
	
	@Length(min=0, max=6, message="平单数量长度必须介于 0 和 6 之间")
	public String getCloseOrderQty() {
		return closeOrderQty;
	}

	public void setCloseOrderQty(String closeOrderQty) {
		this.closeOrderQty = closeOrderQty;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getNewTime() {
		return newTime;
	}

	public void setNewTime(Date newTime) {
		this.newTime = newTime;
	}
	
}