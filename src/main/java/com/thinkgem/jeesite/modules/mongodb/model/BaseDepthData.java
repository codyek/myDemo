package com.thinkgem.jeesite.modules.mongodb.model;

public class BaseDepthData {

	/**  币种  */
    private String symbol;
    /**  time  */
    private Long time;
    /**  卖-总委托额  */
    private Double amountPriceSell;
    /**  卖-总委托量  */
    private Integer totalCountSell;
    /**  卖-平均委托价  */
    private Double avgPriceSell;
    
    /**  买-总委托额  */
    private Double amountPriceBuy;
    /**  买-总委托量  */
    private Integer totalCountBuy;
    /**  买-平均委托价  */
    private Double avgPriceBuy;
    
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Double getAmountPriceSell() {
		return amountPriceSell;
	}
	public void setAmountPriceSell(Double amountPriceSell) {
		this.amountPriceSell = amountPriceSell;
	}
	public Integer getTotalCountSell() {
		return totalCountSell;
	}
	public void setTotalCountSell(Integer totalCountSell) {
		this.totalCountSell = totalCountSell;
	}
	public Double getAvgPriceSell() {
		return avgPriceSell;
	}
	public void setAvgPriceSell(Double avgPriceSell) {
		this.avgPriceSell = avgPriceSell;
	}
	public Double getAmountPriceBuy() {
		return amountPriceBuy;
	}
	public void setAmountPriceBuy(Double amountPriceBuy) {
		this.amountPriceBuy = amountPriceBuy;
	}
	public Integer getTotalCountBuy() {
		return totalCountBuy;
	}
	public void setTotalCountBuy(Integer totalCountBuy) {
		this.totalCountBuy = totalCountBuy;
	}
	public Double getAvgPriceBuy() {
		return avgPriceBuy;
	}
	public void setAvgPriceBuy(Double avgPriceBuy) {
		this.avgPriceBuy = avgPriceBuy;
	}
}
