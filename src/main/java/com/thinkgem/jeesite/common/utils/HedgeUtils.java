package com.thinkgem.jeesite.common.utils;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.modules.log.entity.BitMonitorLog;
import com.thinkgem.jeesite.modules.log.service.BitMonitorLogService;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.entity.account.MarginBalance;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexAccountInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.AccountInterfaceService;
import com.thinkgem.jeesite.modules.sms.SmsService;
import com.thinkgem.jeesite.modules.sms.entity.BitSms;
import com.thinkgem.jeesite.modules.sms.service.BitSmsService;

/**
 *  对冲工具类
* @ClassName: HedgeUtils 
* @author EK huangone 
* @date 2017-11-26 下午9:02:13 
*
 */
public class HedgeUtils {
	protected static Logger log = LoggerFactory.getLogger(HedgeUtils.class);

	// 休眠6秒防止频繁调用超过次数
	public static final long sleepTime_6 = 6000;
	// 休眠15秒防止频繁调用超过次数
	public static final long sleepTime_15 = 15000;
	public static final long sleepTime_30 = 30000;
	public static final long sleepTime_60 = 60000;
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
	
	// 获取mex可用保证金
	public static BigDecimal getMexMarginByHttp(){
		try {
			MexAccountInterfaceService mexAccountSer = SpringContextHolder.getBean(MexAccountInterfaceService.class);
			MarginBalance mb = mexAccountSer.getMargin();
			if(null != mb){
				return mb.getAvailableMargin();
			}
		} catch (Exception e) {
			log.error(">> 获取mex http可用保证金余额信息  error:",e);
		}
		return null;
	}
	
	// 获取mex可用保证金
	public static BigDecimal getMexMarginBySocket(){
		try {
			Object obj = EhCacheUtils.get(Constants.PRICE_CACHE, Constants.CACHE_MEX_MARGIN_KEY);
			if(null != obj){
				return new BigDecimal(obj.toString());
			}
		} catch (Exception e) {
			log.error(">> 获取mex socket可用保证金余额信息  error:",e);
		}
		return null;
	}
	
	// 获取okex可用保证金
	public static BigDecimal getOkexMarginByHttp(){
		try {
			AccountInterfaceService okAccountSer = SpringContextHolder.getBean(AccountInterfaceService.class);
			MarginBalance mb = okAccountSer.getMargin();
			if(null != mb){
				return mb.getAvailableMargin();
			}
		} catch (Exception e) {
			log.error(">> 获取Okex http可用保证金余额信息  error:",e);
		}
		return null;
	}
	
	/**
	 * 更新账户余额
	* @Title: updateAccount
	* @throws
	 */
	public static void updateAccount() {
		try {
			MexAccountInterfaceService mexAccountSer = SpringContextHolder.getBean(MexAccountInterfaceService.class);
			AccountInterfaceService okAccountSer = SpringContextHolder.getBean(AccountInterfaceService.class);
			mexAccountSer.getAccountbalance();
			okAccountSer.getAccountbalance();
		} catch (Exception e) {
			log.error(">> 获取账户余额信息  error:",e);
		}
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
		//System.out.println(">> sign content = "+content);
		String signature = MexHMACSHA256.HMACSHA256(content.getBytes(), apiSecret.getBytes());
		//System.out.println(">> signature = "+signature);
		return signature;
	}
	
	/**
	* @Title: sendSMS 发送短信提醒
	* @param @param uid
	* @param @param mobile
	* @param @param content
	 */
	public static void sendSMS(String uid, String mobile, String content){
		boolean sendFlag = false;
		try {
			SmsService smsService = SpringContextHolder.getBean(SmsService.class);
			sendFlag = smsService.batchSend(mobile, content);
		} catch (Exception e) {
		}
		BitSmsService BitSmsService = SpringContextHolder.getBean(BitSmsService.class);
		BitSms smsEty = new BitSms();
		smsEty.setUseId(uid);
		smsEty.setMobile(mobile);
		smsEty.setContent(content);
		if(sendFlag){
			smsEty.setSendFlag("1");// 成功
		}else{
			smsEty.setSendFlag("0");
		}
		BitSmsService.save(smsEty);
	}
	
	/**
	* @Title: saveLog 保存监控记录
	* @param @param uid
	* @param @param mobile
	* @param @param content
	 */
	public static void saveLog(String uid, String type, String content, String status){
		BitMonitorLogService service = SpringContextHolder.getBean(BitMonitorLogService.class);
		BitMonitorLog log = new BitMonitorLog();
		log.setUseId(uid);
		log.setTypeFlag(type);
		log.setLogContent(content);
		log.setStatusFlag(status);
		service.save(log);
	}
}
