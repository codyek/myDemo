/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.entity.trade;

import org.hibernate.validator.constraints.Length;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 交易主表Entity
 * @author hzf
 * @version 2017-08-27
 */
public class BitTrade extends DataEntity<BitTrade> {
	
	private static final long serialVersionUID = 1L;
	private String code;		// 编码
	private User user;		// 用户id
	private String monitorCode;		// 监控编码
	private String typeFlag;		// 类型：1 宽开窄平, 2 窄开宽平, 3 两者
	private BigDecimal maxAgio;		// 最大差价
	private BigDecimal minAgio;		// 最小差价
	private String symbolA;		// 币种A
	private String symbolB;		// 币种B
	private BigDecimal depositRateA;		// A保证金比率
	private BigDecimal depositRateB;		// B保证金比率
	private Date openBarnTime;		// 开仓时间
	private Date closeBarnTime;		// 平仓时间
	private BigDecimal revenue;		// 收入
	private BigDecimal fee;		// 费用
	private BigDecimal profit;		// 净收入
	private String ifClose;		// 是否已平仓：1 是， 0 否
	private String ifBurstBarn;		// 是否爆仓：1 是， 0 否
	
	public BitTrade() {
		super();
	}

	public BitTrade(String id){
		super(id);
	}

	@Length(min=1, max=100, message="编码长度必须介于 1 和 100 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@NotNull(message="用户id不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=100, message="监控编码长度必须介于 1 和 100 之间")
	public String getMonitorCode() {
		return monitorCode;
	}

	public void setMonitorCode(String monitorCode) {
		this.monitorCode = monitorCode;
	}
	
	@Length(min=1, max=2, message="类型：1 宽开窄平, 2 窄开宽平, 3 两者长度必须介于 1 和 2 之间")
	public String getTypeFlag() {
		return typeFlag;
	}

	public void setTypeFlag(String typeFlag) {
		this.typeFlag = typeFlag;
	}
	
	public BigDecimal getMaxAgio() {
		return maxAgio;
	}

	public void setMaxAgio(BigDecimal maxAgio) {
		this.maxAgio = maxAgio;
	}
	
	public BigDecimal getMinAgio() {
		return minAgio;
	}

	public void setMinAgio(BigDecimal minAgio) {
		this.minAgio = minAgio;
	}
	
	@Length(min=1, max=20, message="币种A长度必须介于 1 和 20 之间")
	public String getSymbolA() {
		return symbolA;
	}

	public void setSymbolA(String symbolA) {
		this.symbolA = symbolA;
	}
	
	@Length(min=1, max=20, message="币种B长度必须介于 1 和 20 之间")
	public String getSymbolB() {
		return symbolB;
	}

	public void setSymbolB(String symbolB) {
		this.symbolB = symbolB;
	}
	
	public BigDecimal getDepositRateA() {
		return depositRateA;
	}

	public void setDepositRateA(BigDecimal depositRateA) {
		this.depositRateA = depositRateA;
	}
	
	public BigDecimal getDepositRateB() {
		return depositRateB;
	}

	public void setDepositRateB(BigDecimal depositRateB) {
		this.depositRateB = depositRateB;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOpenBarnTime() {
		return openBarnTime;
	}

	public void setOpenBarnTime(Date openBarnTime) {
		this.openBarnTime = openBarnTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCloseBarnTime() {
		return closeBarnTime;
	}

	public void setCloseBarnTime(Date closeBarnTime) {
		this.closeBarnTime = closeBarnTime;
	}
	
	public BigDecimal getRevenue() {
		return revenue;
	}

	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}
	
	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	
	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	
	@Length(min=1, max=2, message="是否已平仓：1 是， 0 否长度必须介于 1 和 2 之间")
	public String getIfClose() {
		return ifClose;
	}

	public void setIfClose(String ifClose) {
		this.ifClose = ifClose;
	}
	
	@Length(min=1, max=2, message="是否爆仓：1 是， 0 否长度必须介于 1 和 2 之间")
	public String getIfBurstBarn() {
		return ifBurstBarn;
	}

	public void setIfBurstBarn(String ifBurstBarn) {
		this.ifBurstBarn = ifBurstBarn;
	}
	
}