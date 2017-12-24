package com.thinkgem.jeesite.modules.platform.entity.order;

import java.math.BigDecimal;

public class OkexOrder {

	private String order_id;
	private String symbol;
	private String type;              	// 方向  1,2,3,4
	private String create_date;    		//交易时间
	private BigDecimal price_avg;		// 平均成交价格
	private BigDecimal fee;				// 费用
	private Integer deal_amount;		// 成交数量
	private String status;				// 状态
	
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
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
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public BigDecimal getPrice_avg() {
		return price_avg;
	}
	public void setPrice_avg(BigDecimal price_avg) {
		this.price_avg = price_avg;
	}
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public Integer getDeal_amount() {
		return deal_amount;
	}
	public void setDeal_amount(Integer deal_amount) {
		this.deal_amount = deal_amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
