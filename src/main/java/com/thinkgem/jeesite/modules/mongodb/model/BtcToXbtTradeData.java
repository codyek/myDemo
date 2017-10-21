package com.thinkgem.jeesite.modules.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "BtcXbt_Trade_Data")
public class BtcToXbtTradeData {

	//id属性是给mongodb用的，用@Id注解修饰
    @Id
    private String id;
    /**  time  */
    private Long time;
    /**  ok价  */
    private Double okPrice;
    /**  usd价  */
    private Double mexUSDPrice;
    /**  ok与usd差价  */
    private Double okToUSDAgio;
    
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
	public Double getOkToUSDAgio() {
		return okToUSDAgio;
	}
	public void setOkToUSDAgio(Double okToUSDAgio) {
		this.okToUSDAgio = okToUSDAgio;
	}
}
