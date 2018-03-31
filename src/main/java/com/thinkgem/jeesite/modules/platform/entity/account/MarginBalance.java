package com.thinkgem.jeesite.modules.platform.entity.account;

import java.math.BigDecimal;

public class MarginBalance {

	private BigDecimal walletBalance;		// 钱包余额
	private BigDecimal availableMargin;		// 可用保证金余额
	private BigDecimal marginBalance;		// 保证金余额
	
	public BigDecimal getWalletBalance() {
		return walletBalance;
	}
	public void setWalletBalance(BigDecimal walletBalance) {
		this.walletBalance = walletBalance;
	}
	public BigDecimal getAvailableMargin() {
		return availableMargin;
	}
	public void setAvailableMargin(BigDecimal availableMargin) {
		this.availableMargin = availableMargin;
	}
	public BigDecimal getMarginBalance() {
		return marginBalance;
	}
	public void setMarginBalance(BigDecimal marginBalance) {
		this.marginBalance = marginBalance;
	}
	
}
