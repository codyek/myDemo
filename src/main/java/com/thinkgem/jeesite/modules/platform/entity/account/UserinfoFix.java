package com.thinkgem.jeesite.modules.platform.entity.account;

import java.math.BigDecimal;
import java.util.List;

public class UserinfoFix {
	private Info info;
	private boolean result;
	
	public Info getInfo() {
		return info;
	}
	public void setInfo(Info info) {
		this.info = info;
	}
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
		
	
	public static class Info{
		private BTC btc;
		private LTC ltc;
		public BTC getBtc() {
			return btc;
		}
		public void setBtc(BTC btc) {
			this.btc = btc;
		}
		public LTC getLtc() {
			return ltc;
		}
		public void setLtc(LTC ltc) {
			this.ltc = ltc;
		}
	}
	public static class BTC{
		private BigDecimal balance;		// 账户余额
		private BigDecimal rights;		// 账户权益
		private List<Contracts> contracts;
		public BigDecimal getBalance() {
			return balance;
		}
		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}
		public BigDecimal getRights() {
			return rights;
		}
		public void setRights(BigDecimal rights) {
			this.rights = rights;
		}
		public List<Contracts> getContracts() {
			return contracts;
		}
		public void setContracts(List<Contracts> contracts) {
			this.contracts = contracts;
		}
	}
	public static class LTC{
		private BigDecimal balance;		// 账户余额
		private BigDecimal rights;		// 账户权益
		private List<Contracts> contracts;
		public BigDecimal getBalance() {
			return balance;
		}
		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}
		public BigDecimal getRights() {
			return rights;
		}
		public void setRights(BigDecimal rights) {
			this.rights = rights;
		}
		public List<Contracts> getContracts() {
			return contracts;
		}
		public void setContracts(List<Contracts> contracts) {
			this.contracts = contracts;
		}
	}
	
	public static class Contracts {
		private BigDecimal available;		// 合约可用
		private BigDecimal balance;		// 账户(合约)余额
		private BigDecimal bond;		// 固定保证金
		private String contract_id;		// 合约ID
		private String contract_type;		// 合约类别
		private BigDecimal freeze;		// 冻结
		private BigDecimal profit;		// 已实现盈亏
		private BigDecimal unprofit;		// 未实现盈亏
		private BigDecimal rights;		// 账户权益
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
		public String getContract_id() {
			return contract_id;
		}
		public void setContract_id(String contract_id) {
			this.contract_id = contract_id;
		}
		public String getContract_type() {
			return contract_type;
		}
		public void setContract_type(String contract_type) {
			this.contract_type = contract_type;
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
}