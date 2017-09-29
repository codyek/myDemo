/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.entity.trade;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 币种参数配置Entity
 * @author hzf
 * @version 2017-09-06
 */
public class BitSymbolConfig extends DataEntity<BitSymbolConfig> {
	
	private static final long serialVersionUID = 1L;
	private String code;		// 编码
	private String symbol;		// 币种
	private String platform;		// 平台
	private String feeRate;		// 费率%
	private Integer parValue;		// 一张合约面值
	private String unit;		// 面值单位
	
	public BitSymbolConfig() {
		super();
	}

	public BitSymbolConfig(String id){
		super(id);
	}

	@Length(min=1, max=50, message="编码长度必须介于 1 和 50 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Length(min=1, max=20, message="币种长度必须介于 1 和 20 之间")
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	@Length(min=1, max=20, message="平台长度必须介于 1 和 20 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=1, max=10, message="费率%长度必须介于 1 和 10 之间")
	public String getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}
	
	@NotNull(message="一张合约面值不能为空")
	public Integer getParValue() {
		return parValue;
	}

	public void setParValue(Integer parValue) {
		this.parValue = parValue;
	}
	
	@Length(min=1, max=10, message="面值单位长度必须介于 1 和 10 之间")
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}