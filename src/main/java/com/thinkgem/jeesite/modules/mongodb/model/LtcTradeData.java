package com.thinkgem.jeesite.modules.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Ltc_Trade_Data")
public class LtcTradeData {

	//id属性是给mongodb用的，用@Id注解修饰
    @Id
    private String id;
    /**  time  */
    private Long time;
    /**  ok ltc价  */
    private Double okLTCPrice;
    /**  mex ltc价  */
    private Double mexLTCPrice;
    /**  ok与mex差价  */
    private Double okToMexAgio;
    
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Double getOkLTCPrice() {
		return okLTCPrice;
	}
	public void setOkLTCPrice(Double okLTCPrice) {
		this.okLTCPrice = okLTCPrice;
	}
	public Double getMexLTCPrice() {
		return mexLTCPrice;
	}
	public void setMexLTCPrice(Double mexLTCPrice) {
		this.mexLTCPrice = mexLTCPrice;
	}
	public Double getOkToMexAgio() {
		return okToMexAgio;
	}
	public void setOkToMexAgio(Double okToMexAgio) {
		this.okToMexAgio = okToMexAgio;
	}
	
}
