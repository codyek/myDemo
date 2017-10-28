package com.thinkgem.jeesite.modules.platform.constants;

import java.math.BigDecimal;

public class Constants {

	
	public static final String PRICE_CACHE = "priceCache"; 
	public static final String SYMBOL_BTC_USD = "btc_usd";
	/**   */
	public static final String CACHE_BTCOKEX_PRICE_KEY = "cache:btcokex:price:key";
	
	public static final String SYMBOL_LTC_USD = "ltc_usd";
	/**   */
	public static final String CACHE_LTCOKEX_PRICE_KEY = "cache:ltcokex:price:key"; 
	
	public static final String SYMBOL_XBTUSD = "XBTUSD";
	/**   */
	public static final String CACHE_XBTUSDMEX_PRICE_KEY = "cache:xbtusdmex:price:key"; 
	
	public static final String SYMBOL_XBTQAE = "XBTZ17";
	/**   */
	public static final String CACHE_XBTQAEMEX_PRICE_KEY = "cache:xbtqaemex:price:key"; 
	
	public static final String SYMBOL_LTCQAE = "LTCZ17";
	/**   */
	public static final String CACHE_LTCQAEMEX_PRICE_KEY = "cache:ltcqaemex:price:key";
	
	public static final String SYMBOL_OKEX_TIME = "SYMBOL:OKEX:TIME:key";
	
	public static final String SYMBOL_MEX_TIME = "SYMBOL:MEX:TIME:KEY";
	
	/** 监控操作方式 - 宽开窄平  */
	public static final String TRADE_TYPE_WIDEOPEN = "1"; 
	/** 监控操作方式 - 窄开宽平   */
	public static final String TRADE_TYPE_NARROWOPEN = "2"; 
	/** 监控操作方式 - 宽开窄平、窄开宽平 两者   */
	public static final String TRADE_TYPE_BOTH = "3"; 
	
	public static final BigDecimal BIGDECIMAL_ZERO = new BigDecimal(0);
	
	/**  可开仓  */
	public static final String CAN_OPEN = "canOpen";
	/**  可平仓  */
	public static final String CAN_CLOSE = "canClose";
	/**  操作中  */
	public static final String OPERATING = "operating";
	
	// 明细类型：1 宽开, 2 窄平, 3 窄开, 4 宽平
	/** 交易明细类型 - 宽开  */
	public static final String DETAIL_TYPE_KK = "1"; 
	/** 交易明细类型 - 窄平  */
	public static final String DETAIL_TYPE_ZP = "2"; 
	/** 交易明细类型 - 窄开  */
	public static final String DETAIL_TYPE_ZK = "3"; 
	/** 交易明细类型 - 宽平  */
	public static final String DETAIL_TYPE_KP = "4"; 
	
	// 交易方向：1:开多 BuyUp , 2:开空BuyDown,  3:平多 SellUp,  4:平空SellDown
	// mex  只有 buy 1,4 sell 2,3
	/** 交易方向- 开多  */
	public static final String DIRECTION_BUY_UP = "1"; 
	/** 交易方向 - 开空  */
	public static final String DIRECTION_BUY_DOWN = "2"; 
	/** 交易方向 - 平多  */
	public static final String DIRECTION_SELL_UP = "3"; 
	/** 交易方向 - 平空  */
	public static final String DIRECTION_SELL_DOWN = "4"; 
	
	/** 状态：1 运行中, 1 已成交  */
	public static final String STATUS_RUN = "1"; 
	/** 状态：0 结束 ,  0 委托中*/
	public static final String STATUS_CLOSE = "0"; 
	/** 状态：-1撤销   */
	public static final String STATUS_CANCEL = "-1"; 
	
}
