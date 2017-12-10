package com.thinkgem.jeesite.modules.platform.entity.trade;

import java.math.BigDecimal;

public class TradeTaskReq {

	/**  A币种  */
	private String symbolA;
	/**  B币种  */
	private String symbolB;
	/**  A币种平台  */
	private String platformA;
	/**  B币种平台  */
	private String platformB;
	/**  A币种费率  */
	private BigDecimal feeRateA;
	/**  B币种费率  */
	private BigDecimal feeRateB;
	/**  A合约单张面值  */
	private Integer parValueA;
	/**  B合约单张面值  */
	private Integer parValueB;
	/**  A杠杆  */
	private Integer leverA;
	/**  B杠杆  */
	private Integer leverB;
	/**  A数量  */
	private Integer amountA;
	/**  B数量  */
	private Integer amountB;
	/**  A保证金率  */
	private BigDecimal depositA;
	/**  B保证金率  */
	private BigDecimal depositB;
	/**  最大差价  */
	private BigDecimal maxAgio;
	/**  最小差价  */
	private BigDecimal minAgio;
	/**  A开仓方向  */
	private String openDirA;
	/**  B开仓方向  */
	private String openDirB;
	/**  A爆仓价格  */
	private BigDecimal burstPiceA;
	/**  B爆仓价格  */
	private BigDecimal burstPiceB;
	/**  A换算成价格对应的数量  */
	private BigDecimal priceAmountA;
	/**  B换算成价格对应的数量  */
	private BigDecimal priceAmountB;
	
	/**  T+n 差价回撤率%   */
	private Integer withdrawRate;
	
	/**  是否按收益率平仓  */
	private Boolean closeByProfit;
	/**  正收益率 %  */
	private Integer earnProfit;
	/**  负收益率 %  */
	private Integer deficitProfit;
	
	/**   监控操作方式 - 宽开窄平、窄开宽平or两者  */
	private String type;
	
	/**   停止监控  */
	private String stopJob;
	
	/**   监控主程序code  */
	private String monitorCode;
	
	private Long queryStartTime = 0L; // 开始时间
	private Long queryEndTime = 0L;   // 结束时间
	
	public String getSymbolA() {
		return symbolA;
	}
	public void setSymbolA(String symbolA) {
		this.symbolA = symbolA;
	}
	public String getSymbolB() {
		return symbolB;
	}
	public void setSymbolB(String symbolB) {
		this.symbolB = symbolB;
	}
	public Integer getLeverA() {
		return leverA;
	}
	public void setLeverA(Integer leverA) {
		this.leverA = leverA;
	}
	public Integer getLeverB() {
		return leverB;
	}
	public void setLeverB(Integer leverB) {
		this.leverB = leverB;
	}
	public BigDecimal getDepositA() {
		return depositA;
	}
	public void setDepositA(BigDecimal depositA) {
		this.depositA = depositA;
	}
	public BigDecimal getDepositB() {
		return depositB;
	}
	public void setDepositB(BigDecimal depositB) {
		this.depositB = depositB;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getAmountA() {
		return amountA;
	}
	public void setAmountA(Integer amountA) {
		this.amountA = amountA;
	}
	public Integer getAmountB() {
		return amountB;
	}
	public void setAmountB(Integer amountB) {
		this.amountB = amountB;
	}
	public String getMonitorCode() {
		return monitorCode;
	}
	public void setMonitorCode(String monitorCode) {
		this.monitorCode = monitorCode;
	}
	public String getOpenDirA() {
		return openDirA;
	}
	public void setOpenDirA(String openDirA) {
		this.openDirA = openDirA;
	}
	public String getOpenDirB() {
		return openDirB;
	}
	public void setOpenDirB(String openDirB) {
		this.openDirB = openDirB;
	}
	public BigDecimal getBurstPiceA() {
		return burstPiceA;
	}
	public void setBurstPiceA(BigDecimal burstPiceA) {
		this.burstPiceA = burstPiceA;
	}
	public BigDecimal getBurstPiceB() {
		return burstPiceB;
	}
	public void setBurstPiceB(BigDecimal burstPiceB) {
		this.burstPiceB = burstPiceB;
	}
	public String getPlatformA() {
		return platformA;
	}
	public void setPlatformA(String platformA) {
		this.platformA = platformA;
	}
	public String getPlatformB() {
		return platformB;
	}
	public void setPlatformB(String platformB) {
		this.platformB = platformB;
	}
	public BigDecimal getFeeRateA() {
		return feeRateA;
	}
	public void setFeeRateA(BigDecimal feeRateA) {
		this.feeRateA = feeRateA;
	}
	public BigDecimal getFeeRateB() {
		return feeRateB;
	}
	public void setFeeRateB(BigDecimal feeRateB) {
		this.feeRateB = feeRateB;
	}
	public Integer getParValueA() {
		return parValueA;
	}
	public void setParValueA(Integer parValueA) {
		this.parValueA = parValueA;
	}
	public Integer getParValueB() {
		return parValueB;
	}
	public void setParValueB(Integer parValueB) {
		this.parValueB = parValueB;
	}
	public BigDecimal getPriceAmountA() {
		return priceAmountA;
	}
	public void setPriceAmountA(BigDecimal priceAmountA) {
		this.priceAmountA = priceAmountA;
	}
	public BigDecimal getPriceAmountB() {
		return priceAmountB;
	}
	public void setPriceAmountB(BigDecimal priceAmountB) {
		this.priceAmountB = priceAmountB;
	}
	public String getStopJob() {
		return stopJob;
	}
	public void setStopJob(String stopJob) {
		this.stopJob = stopJob;
	}
	public Long getQueryStartTime() {
		return queryStartTime;
	}
	public void setQueryStartTime(Long queryStartTime) {
		this.queryStartTime = queryStartTime;
	}
	public Long getQueryEndTime() {
		return queryEndTime;
	}
	public void setQueryEndTime(Long queryEndTime) {
		this.queryEndTime = queryEndTime;
	}
	public Integer getWithdrawRate() {
		return withdrawRate;
	}
	public void setWithdrawRate(Integer withdrawRate) {
		this.withdrawRate = withdrawRate;
	}
	public Boolean getCloseByProfit() {
		return closeByProfit;
	}
	public void setCloseByProfit(Boolean closeByProfit) {
		this.closeByProfit = closeByProfit;
	}
	public Integer getEarnProfit() {
		return earnProfit;
	}
	public void setEarnProfit(Integer earnProfit) {
		this.earnProfit = earnProfit;
	}
	public Integer getDeficitProfit() {
		return deficitProfit;
	}
	public void setDeficitProfit(Integer deficitProfit) {
		this.deficitProfit = deficitProfit;
	}
	
	
}
