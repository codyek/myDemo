package com.thinkgem.jeesite.modules.platform.entity.trade;

import java.math.BigDecimal;

public class TradeTaskReq {

	/**  A币种  */
	private String symbolA;
	/**  B币种  */
	private String symbolB;
	/**  A杠杆  */
	private Integer leverA;
	/**  B杠杆  */
	private Integer leverB;
	/**  A数量  */
	private Integer amountA;
	/**  B数量  */
	private Integer amountB;
	/**  A保证金  */
	private BigDecimal depositA;
	/**  B保证金  */
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
	
	/**   监控操作方式 - 宽开窄平、窄开宽平or两者  */
	private String type;
	
	/**   监控主程序code  */
	private String monitorCode;
	
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
	
}
