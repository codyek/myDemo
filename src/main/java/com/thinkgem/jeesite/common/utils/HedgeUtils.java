package com.thinkgem.jeesite.common.utils;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  对冲工具类
* @ClassName: HedgeUtils 
* @author EK huangone 
* @date 2017-11-26 下午9:02:13 
*
 */
public class HedgeUtils {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	// 休眠6秒防止频繁调用超过次数
	public static final long sleepTime_6 = 6000;
	// 休眠15秒防止频繁调用超过次数
	public static final long sleepTime_15 = 15000;
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
	
	
	public static BigDecimal getAccountBySocket(){
		
		return null;
	}
	
	/**
	* @Title: 获取签名
	* A signature is HMAC_SHA256(secret, verb + path + nonce + data), hex encoded.
	* @param @param verb
	* @param @param path
	* @param @param nonce = String.valueOf(System.currentTimeMillis());
	* @param @param apiSecret
	* @return String
	* @throws
	 */
	public static String getMexSocketSign(String nonce, String apiSecret){
		String verb = "GET";
		String path = "/realtime";
		String content = verb + path + nonce;
		System.out.println(">> sign content = "+content);
		String signature = MexHMACSHA256.HMACSHA256(content.getBytes(), apiSecret.getBytes());
		System.out.println(">> signature = "+signature);
		return signature;
	}
	
}
