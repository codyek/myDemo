package com.thinkgem.jeesite.modules.platform.service.okex;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.inter.OkexInterConstants;
import com.thinkgem.jeesite.modules.platform.entity.account.BitOkAccount;
import com.thinkgem.jeesite.modules.platform.entity.account.MarginBalance;
import com.thinkgem.jeesite.modules.platform.service.OkexBaseService;
import com.thinkgem.jeesite.modules.platform.service.account.BitOkAccountService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;


/**
 * 
* @Description: 账户信息接口服务
* @author EK huangone 
* @date 2017-7-9 上午12:38:50 
*
 */
@Service
@Transactional(readOnly = true)
public class AccountInterfaceService extends OkexBaseService{
	
	@Autowired
	private BitOkAccountService bitOkAccountService;
	
	@Transactional
	public BigDecimal getAccountbalance() throws Exception{
		BigDecimal balance = null;
		String json = future_userinfo_4fix();
		//logger.info(">>　okex future_userinfo_4fix json＝"+json);
		if(StringUtils.isNotBlank(json)){
			JSONObject jobJ = JSONObject.parseObject(json);
			if(jobJ.containsKey("result") && jobJ.containsKey("info") && jobJ.getBooleanValue("result")){
				JSONObject infoObject = jobJ.getJSONObject("info");
				if(infoObject.containsKey("btc")){
					JSONObject btcObj = infoObject.getJSONObject("btc");
					balance = btcObj.getBigDecimal("balance");
					balance = balance.setScale(6,BigDecimal.ROUND_HALF_UP);
					// 保存到数据库 
					saveOkexAccount(balance);
				}
			}
		}
		return balance;
	}
	
	// 保存Okex账户btc_usd余额
	private void saveOkexAccount(BigDecimal balance){
		BitOkAccount bitOkAccount = new BitOkAccount();
		User user = UserUtils.getUser();
		bitOkAccount.setUseId(user.getId());
		bitOkAccount.setSymbol("btc_usd");
		List<BitOkAccount> list = bitOkAccountService.findList(bitOkAccount);
		if(null != list && !list.isEmpty()){
			bitOkAccount = list.get(0);
		}
		bitOkAccount.setBalance(balance);
		bitOkAccount.setAccountBalance(balance);
		bitOkAccountService.save(bitOkAccount);
		logger.info(">>>　save okex Account ok!");
	}
	
	/**
	* 获取您帐户的保证金余额
	 * @throws Exception 
	*/
	public MarginBalance getMargin() throws Exception{
		MarginBalance margin = null;
		String json = future_userinfo_4fix();
		if(StringUtils.isNotBlank(json)){
			JSONObject jobJ = JSONObject.parseObject(json);
			if(jobJ.containsKey("result") && jobJ.containsKey("info") && jobJ.getBooleanValue("result")){
				JSONObject infoObject = jobJ.getJSONObject("info");
				if(infoObject.containsKey("btc")){
					margin = new MarginBalance();
					JSONObject btcObj = infoObject.getJSONObject("btc");
					BigDecimal balance = btcObj.getBigDecimal("balance");
					balance = balance.setScale(6,BigDecimal.ROUND_HALF_UP);
					margin.setAvailableMargin(balance);
					if(jobJ.containsKey("rights")){
						BigDecimal walBalance = jobJ.getBigDecimal("rights");
						walBalance = walBalance.setScale(6,BigDecimal.ROUND_HALF_UP);
						margin.setWalletBalance(walBalance);
					}
				}
			}
		}
		return margin;
	}
	
	/**
	* 获取OKEX合约账户信息（全仓）
	* @return String
	* @throws Exception
	 */
	public String future_userinfo() throws Exception{
		String url = OkexInterConstants.PFUTURE_USERINFO_URL;
		Map<String,String> params = new HashMap<String, String>();
		return doPost(url, params);
	}
	
	/**
	* 获取用户持仓获取OKEX合约账户信息（全仓）
	* @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
	* @return String
	* @throws Exception
	 */
	public String future_position(String symbol, String contractType) throws Exception{
		String url = OkexInterConstants.PFUTURE_POSITION_URL;
		Map<String,String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("contract_type", contractType);
		return doPost(url, params);
	}
	
	/**
	* 获取OKEX合约交易历史（非个人）
	* @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param date 合约交割时间，格式yyyy-MM-dd
    * @param since 交易Id起始位置
	* @return String
	* @throws Exception
	 */
	public String future_trades_history(String symbol, String date, Long since) throws Exception{
		String url = OkexInterConstants.PFUTURE_TRADES_HISTORY_URL;
		Map<String,String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("date", date);
		params.put("since", String.valueOf(since));
		return doPost(url, params);
	}
	
	/**
	* 获取逐仓合约账户信息
	* @return String
	* @throws Exception
	 */
	public String future_userinfo_4fix() throws Exception{
		String url = OkexInterConstants.PFUTURE_USERINFO_4FIX_URL;
		Map<String,String> params = new HashMap<String, String>();
		return doPost(url, params);
	}
	
	/**
	* 逐仓用户持仓查询
	* @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
    * @param type 默认返回10倍杠杆持仓 type=1 返回全部持仓数据
	* @return String
	* @throws Exception
	 */
	public String future_position_4fix(String symbol, String contractType, String type) throws Exception{
		String url = OkexInterConstants.PFUTURE_POSITION_4FIX_URL;
		Map<String,String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("contract_type", contractType);
		params.put("type", type);
		return doPost(url, params);
	}
}
