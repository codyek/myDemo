package com.thinkgem.jeesite.modules.platform.entity.account;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * Ok账户信息表Entity
 * @author hzf
 * @version 2017-09-09
 */
public class BitOkAccount extends DataEntity<BitOkAccount> {
	
	private static final long serialVersionUID = 1L;
	private String useId;		// 用户id
	private String symbol;		// 币种
	private BigDecimal accountBalance;		// 账户余额
	private BigDecimal available;		// 合约可用
	private BigDecimal balance;		// 账户(合约)余额
	private BigDecimal bond;		// 固定保证金
	private String contractId;		// 合约ID
	private String contractType;		// 合约类别
	private BigDecimal freeze;		// 冻结
	private BigDecimal profit;		// 已实现盈亏
	private BigDecimal unprofit;		// 未实现盈亏
	private BigDecimal rights;		// 账户权益
	
	public BitOkAccount() {
		super();
	}

	public BitOkAccount(String id){
		super(id);
	}

	@Length(min=1, max=100, message="用户id长度必须介于 1 和 100 之间")
	public String getUseId() {
		return useId;
	}

	public void setUseId(String useId) {
		this.useId = useId;
	}
	
	@Length(min=1, max=20, message="币种长度必须介于 1 和 20 之间")
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}
	
	public BigDecimal getAvailable() {
		return available;
	}

	public void setAvailable(BigDecimal available) {
		this.available = available;
	}
	
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	public BigDecimal getBond() {
		return bond;
	}

	public void setBond(BigDecimal bond) {
		this.bond = bond;
	}
	
	@Length(min=1, max=50, message="合约ID长度必须介于 1 和 50 之间")
	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	
	@Length(min=1, max=50, message="合约类别长度必须介于 1 和 50 之间")
	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	
	public BigDecimal getFreeze() {
		return freeze;
	}

	public void setFreeze(BigDecimal freeze) {
		this.freeze = freeze;
	}
	
	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	
	public BigDecimal getUnprofit() {
		return unprofit;
	}

	public void setUnprofit(BigDecimal unprofit) {
		this.unprofit = unprofit;
	}
	
	public BigDecimal getRights() {
		return rights;
	}

	public void setRights(BigDecimal rights) {
		this.rights = rights;
	}
	
}