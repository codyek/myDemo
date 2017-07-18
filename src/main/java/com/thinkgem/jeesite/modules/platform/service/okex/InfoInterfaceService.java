package com.thinkgem.jeesite.modules.platform.service.okex;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.utils.HttpUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.inter.OkexInterConstants;
import com.thinkgem.jeesite.modules.platform.service.OkexBaseService;

/**
 * 
 * @Description: 市场信息接口服务
 * @author EK huangone
 * @date 2017-7-9 上午12:38:50
 * 
 */
@Service
@Transactional(readOnly = true)
public class InfoInterfaceService extends OkexBaseService {

	/**
	 * 
	 * @Title: 获取OKEX合约行情
	 * @param symbol
	 *            btc_usd:比特币 ltc_usd :莱特币
	 * @param contractType
	 *            合约类型: this_week:当周 next_week:下周 month:当月 quarter:季度
	 * @return String
	 * @throws Exception
	 */
	public String future_ticker(String symbol, String contractType) throws Exception {

		Map<String, String> params = new HashMap<String, String>();

		if (StringUtils.isNotEmpty(symbol)) {
			params.put("symbol", symbol);
		}
		if (StringUtils.isNotEmpty(contractType)) {
			params.put("contract_type", contractType);
		}
		String result = HttpUtils.httpsGet(OkexInterConstants.GFUTURE_TICKER_URL, params);
		System.out.println(result);
		return result;
	}

	/**
	 * 获取OKEX合约深度信息
	 * 
	 * @param symbol
	 *            btc_usd:比特币 ltc_usd :莱特币
	 * @param contractType
	 *            合约类型: this_week:当周 next_week:下周 month:当月 quarter:季度
	 * @param size
	 *            value: 1-200
	 * @param merge
	 *            value: 1 （合并深度）
	 * @return
	 * @throws Exception
	 */
	public String future_depth(String symbol, String contractType,
			Integer size, Integer merge) throws Exception {
		Map<String, String> params = new HashMap<String, String>();

		if (StringUtils.isNotEmpty(symbol)) {
			params.put("symbol", symbol);
		}
		if (StringUtils.isNotEmpty(contractType)) {
			params.put("contract_type", contractType);
		}
		if (null != size) {
			params.put("size", String.valueOf(size));
		}
		if (null != merge) {
			params.put("merge", String.valueOf(merge));
		}
		String result = HttpUtils.httpsGet(OkexInterConstants.GFUTURE_DEPTH_URL, params);
		System.out.println(result);
		return result;
	}

	/**
	 * 获取OKEX合约交易记录信息
	 * 
	 * @param symbol
	 *            btc_usd:比特币 ltc_usd :莱特币
	 * @param contractType
	 *            合约类型: this_week:当周 next_week:下周 month:当月 quarter:季度
	 * @return
	 * @throws Exception
	 */
	public String future_trades(String symbol, String contractType) throws Exception {
		Map<String, String> params = new HashMap<String, String>();

		if (StringUtils.isNotEmpty(symbol)) {
			params.put("symbol", symbol);
		}
		if (StringUtils.isNotEmpty(contractType)) {
			params.put("contract_type", contractType);
		}
		String result = HttpUtils.httpsGet(OkexInterConstants.GFUTURE_TRADES_URL, params);
		System.out.println(result);
		return result;
	}

	/**
	 * 获取OKEX合约指数信息
	 * 
	 * @param symbol
	 *            btc_usd:比特币 ltc_usd :莱特币
	 * @return
	 * @throws Exception
	 */
	public String future_index(String symbol) throws Exception {
		Map<String, String> params = new HashMap<String, String>();

		if (StringUtils.isNotEmpty(symbol)) {
			params.put("symbol", symbol);
		}
		String result = HttpUtils.httpsGet(OkexInterConstants.GFUTURE_INDEX_URL, params);
		System.out.println(result);
		return result;
	}
	
	/**
     * 获取美元人民币汇率
     * @return
     * @throws Exception 
    */
    public String exchange_rate() throws Exception{
    	Map<String, String> params = new HashMap<String, String>();
		String result = HttpUtils.httpsGet(OkexInterConstants.GEXCHANGE_RATE_URL, params);
		System.out.println(result);
		return result;
    }
    
    /**
     * 获取交割预估价
     * @param symbol
	 *            btc_usd:比特币 ltc_usd :莱特币
     * @return
     * @throws Exception 
    */
    public String future_estimated_price(String symbol) throws Exception{
    	Map<String, String> params = new HashMap<String, String>();
    	if (StringUtils.isNotEmpty(symbol)) {
			params.put("symbol", symbol);
		}
		String result = HttpUtils.httpsGet(OkexInterConstants.GFUTURE_ESTIMATED_PRICE_URL, params);
		System.out.println(result);
		return result;
    }
    
    /**
     * 获取虚拟合约的K线数据
     * @return
     * @throws Exception 
    */
    /**
     * 
    * @Title: 获取虚拟合约的K线数据
    * @param @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
    * @param @param type 周期 1min:1分钟， 3min:3分钟， 5min:5分钟， 15min:15分钟
    * @param @param size 指定获取数据的条数
    * @param @param since 时间戳（eg：1417536000000）。 返回该时间戳以后的数据
    * @return String
    * @throws Exception
     */
    public String future_kline(String symbol, String contractType,
			String type, Integer size, Long since) throws Exception{
    	Map<String, String> params = new HashMap<String, String>();
    	if (StringUtils.isNotEmpty(symbol)) {
			params.put("symbol", symbol);
		}
		if (StringUtils.isNotEmpty(contractType)) {
			params.put("contract_type", contractType);
		}
		if (StringUtils.isNotEmpty(type)) {
			params.put("type", type);
		}
		if (null != size) {
			params.put("size", String.valueOf(size));
		}
		if (null != since) {
			params.put("merge", String.valueOf(since));
		}
		String result = HttpUtils.httpsGet(OkexInterConstants.GFUTURE_KLINE_URL, params);
		System.out.println(result);
		return result;
    }
    
    /**
     * 获取当前可用合约总持仓量
     * @param @param symbol btc_usd：比特币， ltc_usd：莱特币
     * @param @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
     * @return
     * @throws Exception 
    */
    public String future_hold_amount(String symbol, String contractType) throws Exception{
    	Map<String, String> params = new HashMap<String, String>();
    	if (StringUtils.isNotEmpty(symbol)) {
			params.put("symbol", symbol);
		}
		if (StringUtils.isNotEmpty(contractType)) {
			params.put("contract_type", contractType);
		}
		String result = HttpUtils.httpsGet(OkexInterConstants.GFUTURE_HOLD_AMOUNT_URL, params);
		System.out.println(result);
		return result;
    }
    
    /**
     * 获取合约最高买价和最低卖价
     * @param @param symbol btc_usd：比特币， ltc_usd：莱特币
     * @param @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
     * @return
     * @throws Exception 
    */
    public String future_price_limit(String symbol, String contractType) throws Exception{
    	Map<String, String> params = new HashMap<String, String>();
    	if (StringUtils.isNotEmpty(symbol)) {
			params.put("symbol", symbol);
		}
		if (StringUtils.isNotEmpty(contractType)) {
			params.put("contract_type", contractType);
		}
		String result = HttpUtils.httpsGet(OkexInterConstants.GFUTURE_PRICE_LIMIT_URL, params);
		System.out.println(result);
		return result;
    }
    
}
