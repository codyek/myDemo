package com.thinkgem.jeesite.modules.platform.entity.order;

import java.math.BigDecimal;

public class MexOrder {

	private String orderID;
	private String symbol;
	private String side;              // 方向  Buy，Sell
	private String transactTime;    //交易时间
	private Long transactTimeLong;    //交易时间
	private BigDecimal avgPx;		// 平均成交价格
	private Integer cumeQty;		// 成交数量
	private String text;
	
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String getTransactTime() {
		return transactTime;
	}
	public void setTransactTime(String transactTime) {
		this.transactTime = transactTime;
	}
	public BigDecimal getAvgPx() {
		return avgPx;
	}
	public void setAvgPx(BigDecimal avgPx) {
		this.avgPx = avgPx;
	}
	public Integer getCumeQty() {
		return cumeQty;
	}
	public void setCumeQty(Integer cumeQty) {
		this.cumeQty = cumeQty;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Long getTransactTimeLong() {
		return transactTimeLong;
	}
	public void setTransactTimeLong(Long transactTimeLong) {
		this.transactTimeLong = transactTimeLong;
	}
	
}
