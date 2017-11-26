package com.thinkgem.jeesite.common.utils;

import java.math.BigDecimal;

/**
 *  对冲工具类
* @ClassName: HedgeUtils 
* @author EK huangone 
* @date 2017-11-26 下午9:02:13 
*
 */
public class HedgeUtils {

	/**
	 * 计算收益率 
	 * @return Double
	 */
	public static Double countProfitRate(BigDecimal openBuy, BigDecimal openSell, 
			BigDecimal closeBuy, BigDecimal closeSell){
		Double ret = 0D;
		// 头寸Quota
		BigDecimal Quota = new BigDecimal(1000);
		BigDecimal amountBuy;
		BigDecimal amountSell;
		
		amountBuy = Quota.divide(openBuy);
		amountSell = Quota.divide(openSell);
		
		BigDecimal sumBuy = amountBuy.multiply(closeBuy);
		BigDecimal sumSell = amountSell.multiply(closeSell);
		
		BigDecimal sum = sumBuy.add(sumSell);
		
		BigDecimal quotaAll = Quota.add(Quota);
		BigDecimal profit = sum.subtract(quotaAll);
		ret = profit.divide(quotaAll).doubleValue();
		
		return ret;
	}
	
	// 计算净收益
	public static BigDecimal countProfit(BigDecimal openBuy, Double profitRate){
		return openBuy.multiply(new BigDecimal(profitRate));
	}
	
}
