package com.thinkgem.jeesite.modules.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Btc_Trade_Data")
public class BtcTradeData {

	//id属性是给mongodb用的，用@Id注解修饰
    @Id
    private String id;
    /**  time  */
    private Long time;
    /**  ok价  */
    private Double okPrice;
    /**  usd价  */
    private Double mexUSDPrice;
    /**  u17价  */
    private Double mexU17Price;
    /**  ok与usd差价  */
    private Double okToUSDAgio;
    /**  ok与u17差价  */
    private Double okToU17Agio;
    /**  usd与u17差价  */
    private Double usdToU17Agio;
    
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Double getOkPrice() {
		return okPrice;
	}
	public void setOkPrice(Double okPrice) {
		this.okPrice = okPrice;
	}
	public Double getMexUSDPrice() {
		return mexUSDPrice;
	}
	public void setMexUSDPrice(Double mexUSDPrice) {
		this.mexUSDPrice = mexUSDPrice;
	}
	public Double getMexU17Price() {
		return mexU17Price;
	}
	public void setMexU17Price(Double mexU17Price) {
		this.mexU17Price = mexU17Price;
	}
	public Double getOkToUSDAgio() {
		return okToUSDAgio;
	}
	public void setOkToUSDAgio(Double okToUSDAgio) {
		this.okToUSDAgio = okToUSDAgio;
	}
	public Double getOkToU17Agio() {
		return okToU17Agio;
	}
	public void setOkToU17Agio(Double okToU17Agio) {
		this.okToU17Agio = okToU17Agio;
	}
	public Double getUsdToU17Agio() {
		return usdToU17Agio;
	}
	public void setUsdToU17Agio(Double usdToU17Agio) {
		this.usdToU17Agio = usdToU17Agio;
	}
    
}
