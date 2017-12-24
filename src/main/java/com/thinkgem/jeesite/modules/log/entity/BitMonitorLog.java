package com.thinkgem.jeesite.modules.log.entity;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 监控记录表Entity
 * @author hzf
 * @version 2017-12-23
 */
public class BitMonitorLog extends DataEntity<BitMonitorLog> {
	
	private static final long serialVersionUID = 1L;
	private String useId;		// 用户id
	private String typeFlag;		// 类型：monitor监控,trade交易
	private String logContent;		// 日志内容
	private String statusFlag;		// 状态：1 成功, 0 失败
	
	public BitMonitorLog() {
		super();
	}

	public BitMonitorLog(String id){
		super(id);
	}

	@Length(min=1, max=100, message="用户id长度必须介于 1 和 100 之间")
	public String getUseId() {
		return useId;
	}

	public void setUseId(String useId) {
		this.useId = useId;
	}
	
	@Length(min=1, max=10, message="类型：monitor监控,trade交易长度必须介于 1 和 10 之间")
	public String getTypeFlag() {
		return typeFlag;
	}

	public void setTypeFlag(String typeFlag) {
		this.typeFlag = typeFlag;
	}
	
	@Length(min=1, max=1200, message="日志内容长度必须介于 1 和 1200 之间")
	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	
	@Length(min=0, max=2, message="状态：1 成功, 0 失败长度必须介于 0 和 2 之间")
	public String getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(String statusFlag) {
		this.statusFlag = statusFlag;
	}
	
}