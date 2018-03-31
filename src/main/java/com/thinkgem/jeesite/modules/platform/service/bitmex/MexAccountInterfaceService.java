package com.thinkgem.jeesite.modules.platform.service.bitmex;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.inter.BitMexInterConstants;
import com.thinkgem.jeesite.modules.platform.entity.account.BitMexAccount;
import com.thinkgem.jeesite.modules.platform.entity.account.MarginBalance;
import com.thinkgem.jeesite.modules.platform.service.MexBaseService;
import com.thinkgem.jeesite.modules.platform.service.account.BitMexAccountService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;


/**
 * 
* @Description: 账户接口服务
* @author EK huangone 
* @date 2017-7-9 上午12:38:50 
*
 */
@Service
@Transactional(readOnly = true)
public class MexAccountInterfaceService extends MexBaseService{
	@Autowired
	private BitMexAccountService bitMexAccountService;
	
	@Transactional
	public BigDecimal getAccountbalance() throws Exception{
		BigDecimal balance = null;
		String json = get_user_margin("XBt");
		//logger.info(">>  bitmex get_user_margin = "+json);
		if(StringUtils.isNotBlank(json)){
			JSONObject jobJ = JSONObject.parseObject(json);
			if(jobJ.containsKey("marginBalance")){
				balance = jobJ.getBigDecimal("marginBalance");
				// mex XBt 转 XBT 1XBt = 0.00000001XBT
				balance = balance.multiply(new BigDecimal(0.00000001));
				balance = balance.setScale(6,BigDecimal.ROUND_HALF_UP);
				// 保存到数据库 
				saveMexAccount(balance);
			}
		}
		return balance;
	}
	
	// 保存Mex账户XBTUSD余额
	private void saveMexAccount(BigDecimal balance){
		BitMexAccount bitMexAccount = new BitMexAccount();
		User user = UserUtils.getUser();
		bitMexAccount.setUseId(user.getId());
		bitMexAccount.setSymbol("XBTUSD");
		List<BitMexAccount> list = bitMexAccountService.findList(bitMexAccount);
		if(null != list && !list.isEmpty()){
			bitMexAccount = list.get(0);
		}
		bitMexAccount.setBalance(balance);
		bitMexAccount.setAccountBalance(balance);
		bitMexAccountService.save(bitMexAccount);
		logger.info(">>>　save BitMex Account ok!");
	}
	
	/**
	* 获取您帐户的保证金余额
	 * @throws Exception 
	*/
	public MarginBalance getMargin() throws Exception{
		MarginBalance margin = null;
		String json = get_user_margin("XBt");
		//logger.info(">>  bitmex get_user_margin = "+json);
		if(StringUtils.isNotBlank(json)){
			JSONObject jobJ = JSONObject.parseObject(json);
			if(jobJ.containsKey("availableMargin")){
				margin = new MarginBalance();
				BigDecimal balance = jobJ.getBigDecimal("availableMargin");
				// mex XBt 转 XBT 1XBt = 0.00000001XBT
				balance = balance.multiply(new BigDecimal(0.00000001));
				balance = balance.setScale(8,BigDecimal.ROUND_HALF_UP);
				margin.setAvailableMargin(balance);
				if(jobJ.containsKey("walletBalance")){
					BigDecimal walBalance = jobJ.getBigDecimal("walletBalance");
					walBalance = walBalance.multiply(new BigDecimal(0.00000001));
					walBalance = walBalance.setScale(8,BigDecimal.ROUND_HALF_UP);
					margin.setWalletBalance(walBalance);
				}
				if(jobJ.containsKey("marginBalance")){
					BigDecimal marBalance = jobJ.getBigDecimal("marginBalance");
					marBalance = marBalance.multiply(new BigDecimal(0.00000001));
					marBalance = marBalance.setScale(8,BigDecimal.ROUND_HALF_UP);
					margin.setMarginBalance(marBalance);
				}
			}
		}
		return margin;
	}
	
	/**
	* 获取您帐户的保证金状态
	* @param currency 货币：XBt， XBT
	* @return String
	* @throws Exception
	 */
	public String get_user_margin(String currency) throws Exception {
		String url = BitMexInterConstants.GET_USER_MARGIN_URL;
		JSONObject param = new JSONObject();
		param.put("currency", currency);
		return exchange(url, HttpMethod.GET, true, param);
	}
	
	/**
	* 获取您当前的钱包信息
	* @param currency 货币：XBt， XBT
	* @return String
	* @throws Exception
	 */
	public String get_user_wallet(String currency) throws Exception {
		String url = BitMexInterConstants.GET_USER_WALLET_URL;
		JSONObject param = new JSONObject();
		param.put("currency", currency);
		return exchange(url, HttpMethod.GET, true, param);
	}
	
	/**
	* 获取您当前的余额数据
	* @param currency 货币：XBt， XBT
	* @return String
	* @throws Exception
	 */
	public String get_user_execution(String currency) throws Exception {
		String url = BitMexInterConstants.GET_USER_EXECUTION_URL;
		JSONObject param = new JSONObject();
		param.put("currency", currency);
		return exchange(url, HttpMethod.GET, true, param);
	}
	
	/**
	* 获取您帐户信息
	* @param currency 货币：XBt， XBT
	* @return String
	* @throws Exception
	 */
	public String get_user() throws Exception {
		String url = BitMexInterConstants.GET_USER_URL;
		JSONObject param = new JSONObject();
		return exchange(url, HttpMethod.GET, true, param);
	}
	
	/**
	* 获取钱包交易历史数据
	* @param currency 货币：XBt， XBT
	* @return String
	* @throws Exception
	 */
	public String get_user_walletHistory(String currency) throws Exception {
		String url = BitMexInterConstants.GET_USER_WALLETHISTORY_URL;
		JSONObject param = new JSONObject();
		param.put("currency", currency);
		return exchange(url, HttpMethod.GET, true, param);
	}
	
	/**
	* 获取所有钱包交易数据
	* @param currency 货币：XBt， XBT
	* @return String
	* @throws Exception
	 */
	public String get_user_walletSummary(String currency) throws Exception {
		String url = BitMexInterConstants.GET_USER_WALLETSUMMARY_URL;
		JSONObject param = new JSONObject();
		param.put("currency", currency);
		return exchange(url, HttpMethod.GET, true, param);
	}

	/**
	 * 修改公开订单的数量或价格
	 * TODO if use 
	 */
	
}
