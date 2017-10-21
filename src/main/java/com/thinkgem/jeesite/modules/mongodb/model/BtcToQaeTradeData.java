package com.thinkgem.jeesite.modules.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "BtcQae_Trade_Data")
public class BtcToQaeTradeData {

	//id属性是给mongodb用的，用@Id注解修饰
    @Id
    private String id;
    /**  time  */
    private Long time;
    /**  ok价  */
    private Double okPrice;
    /**  u17价  */
    private Double mexQaePrice;
    /**  ok与u17差价  */
    private Double okToQaeAgio;
    
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
	public Double getMexQaePrice() {
		return mexQaePrice;
	}
	public void setMexQaePrice(Double mexQaePrice) {
		this.mexQaePrice = mexQaePrice;
	}
	public Double getOkToQaeAgio() {
		return okToQaeAgio;
	}
	public void setOkToQaeAgio(Double okToQaeAgio) {
		this.okToQaeAgio = okToQaeAgio;
	}

}
