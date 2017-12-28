package com.thinkgem.jeesite.modules.platform.entity.order;

import java.math.BigDecimal;

public class OkexOrder {

	private String orderId;
	private String symbol;
	private String type;              	// 方向  1,2,3,4
	private String typeString;              	// 方向  1,2,3,4
	private String createDate;    		//交易时间
	private Long createDateLong;    		//交易时间
	private BigDecimal priceAvg;		// 平均成交价格
	private BigDecimal fee;				// 费用
	private Integer dealAmount;		// 成交数量
	private String status;				// 状态
	private String statusString;				// 状态
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeString() {
		return typeString;
	}
	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public Long getCreateDateLong() {
		return createDateLong;
	}
	public void setCreateDateLong(Long createDateLong) {
		this.createDateLong = createDateLong;
	}
	public BigDecimal getPriceAvg() {
		return priceAvg;
	}
	public void setPriceAvg(BigDecimal priceAvg) {
		this.priceAvg = priceAvg;
	}
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public Integer getDealAmount() {
		return dealAmount;
	}
	public void setDealAmount(Integer dealAmount) {
		this.dealAmount = dealAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusString() {
		return statusString;
	}
	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}
	
	// type转TypeString
	public static String getTypeStr(String type){
		String typeStr = "";
		if ("1".equals(type)) {
			typeStr = "开多";
		} else if ("2".equals(type)) {
			typeStr = "开空";
		} else if ("3".equals(type)) {
			typeStr = "平多";
		} else if ("4".equals(type)) {
			typeStr = "平空";
		}
		return typeStr;
	}
	
	// status转statusStr
	public static String getStatusStr(String status){
		String statusStr = "";//订单状态(0等待成交 1部分成交 2全部成交 -1撤单 4撤单处理中 5撤单中)
		if ("0".equals(status)) {
			statusStr = "等待成交";
		} else if ("1".equals(status)) {
			statusStr = "部分成交";
		} else if ("2".equals(status)) {
			statusStr = "全部成交";
		} else {
			statusStr = "撤单";
		}
		return statusStr;
	}
}
