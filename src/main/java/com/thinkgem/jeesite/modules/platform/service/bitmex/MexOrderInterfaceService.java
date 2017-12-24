package com.thinkgem.jeesite.modules.platform.service.bitmex;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.inter.BitMexInterConstants;
import com.thinkgem.jeesite.modules.platform.entity.order.MexOrder;
import com.thinkgem.jeesite.modules.platform.service.MexBaseService;


/**
 * 
* @Description: 订单接口服务
* @author EK huangone 
* @date 2017-7-9 上午12:38:50 
*
 */
@Service
@Transactional(readOnly = true)
public class MexOrderInterfaceService extends MexBaseService{
	
	/**
	* 设置杠杆数
	* @param symbol XBTUSD：比特币，
    * @param leverage 价格
	* @throws Exception
	 */
	public String post_leverage(String symbol, Double leverage) throws Exception {
		String url = BitMexInterConstants.POST_POSITION_LEVERAGE_URL;
		JSONObject param = new JSONObject();
		param.put("symbol", symbol);
		param.put("leverage", leverage);
		return exchange(url, HttpMethod.POST, true, param);
	}
	
	/**
	* 创建新订单
	* @param symbol XBTUSD：比特币，
    * @param ordType 订单类型。：市场(Market)，限制(Limit)，停止(Stop)，停止限制(StopLimit)，市场交易
    * @param price 价格
    * @param orderQty 委托数量
    * @param side Buy, Sell.
    * @param text 
	* @return String
	* @throws Exception
	 */
	public String post_order(String symbol, String ordType,
			Double price, Double orderQty, String side, 
			String text) throws Exception {
		String url = BitMexInterConstants.POST_ORDER_URL;
		JSONObject param = new JSONObject();
		param.put("symbol", symbol);
		param.put("ordType", ordType);
		if(!"Market".equals(ordType)){ // 市场价 无price入参
			param.put("price", price);
		}
		param.put("orderQty", orderQty);
		param.put("side", side);
		param.put("text", text);
		return exchange(url, HttpMethod.POST, true, param);
	}
	
	/**
	 * 获取你的订单
	 * @param symbol
	 *            XBTUSD：比特币， ltc：莱特币
	 * @param count
	 *            要获取的结果数
	 * @return String
	 * @throws Exception
	 */
	public String get_order(String symbol, Double count) throws Exception {
		String url = BitMexInterConstants.GET_ORDER_URL;
		JSONObject param = new JSONObject();
		param.put("symbol", symbol);
		param.put("count", count);
		param.put("reverse", true);
		return exchange(url, HttpMethod.GET, true, param);
	}
	
	/**
	 * 获取你的订单
	 * @param symbol
	 *            XBTUSD：比特币， ltc：莱特币
	 * @param count
	 *            要获取的结果数
	 * @return String
	 * @throws Exception
	 */
	public List<MexOrder> getOrderInfo(String symbol, Double count) throws Exception {
		List<MexOrder> orders = null;
		String json = get_order(symbol,count);
		if(StringUtils.isNotBlank(json)){
			JSONArray jsonArr = JSONObject.parseArray(json);
			if(null != jsonArr && !jsonArr.isEmpty()){
				orders = new ArrayList<MexOrder>();
				for (int i = 0; i < jsonArr.size(); i++) {
					JSONObject js = jsonArr.getJSONObject(i);
					MexOrder order = new MexOrder();
					order.setOrderID(js.getString("orderID"));
					order.setSymbol(js.getString("symbol"));
					order.setSide(js.getString("side"));
					BigDecimal px = js.getBigDecimal("avgPx");
					if(null != px){
						order.setAvgPx(px);
					}else{
						order.setAvgPx(new BigDecimal(0));
					}
					order.setCumeQty(js.getInteger("cumQty"));
					java.sql.Timestamp time = js.getTimestamp("transactTime");
					if(null != time){
						order.setTransactTime(time.toString());
					}else{
						order.setTransactTime("");
					}
					order.setText(js.getString("text"));
					orders.add(order);
				}
			}
		}
		return orders;
	}
	
	/**
	 * 取消订单, 发送多个订单ID批量取消
	 * @param orderID
	 * @param text
	 *            可选,取消注释。 例如 “超越”。
	 * @return String
	 * @throws Exception
	 */
	public String delete_order(String orderID, String text) throws Exception {
		String url = BitMexInterConstants.DEL_ORDER_URL;
		JSONObject param = new JSONObject();
		param.put("orderID", orderID);
		if(StringUtils.isNotEmpty(text)){
			param.put("text", text);
		}
		return exchange(url, HttpMethod.DELETE, true, param);
	}
	
	/**
	 * 取消您的所有订单
	 * @param symbol
	 *            XBTUSD：比特币， ltc_usd：莱特币
	 * @param filter
	 *            可选过滤器取消。 仅用于取消一些订单，例如 {“side”：“Buy”}。
	 * @param text
	 *            可选,注释
	 * @return String
	 * @throws Exception
	 */
	public String delete_order_all(String symbol, String filter, String text) throws Exception {
		String url = BitMexInterConstants.DEL_ORDER_ALL_URL;
		JSONObject param = new JSONObject();
		param.put("symbol", symbol);
		if(StringUtils.isNotEmpty(filter)){
			param.put("filter", filter);
		}
		if(StringUtils.isNotEmpty(text)){
			param.put("text", text);
		}
		return exchange(url, HttpMethod.DELETE, true, param);
	}
	
	/**
	 * 以垂直格式获取当前订单
	 * @param symbol
	 *            XBTUSD：比特币， XBTU17, LTCU17：莱特币
	 * @param depth
	 *            订单的深度, 0:全面深度。
	 * @return String
	 * @throws Exception
	 */
	public String get_orderBookL2(String symbol, Double depth) throws Exception {
		String url = BitMexInterConstants.GET_ORDERBOOK_L2_URL;
		JSONObject param = new JSONObject();
		param.put("symbol", symbol);
		param.put("depth", depth);
		return exchange(url, HttpMethod.GET, true, param);
	}
	
	/**
	 * 修改公开订单的数量或价格
	 * TODO if use 
	 */
	
	/**
	 * 修改相同符号的多个订单
	 * TODO if use 
	 */
	
	/**
	 * 为同一符号创建多个新订单
	 * TODO if use 
	 */
	
	/**
	 * 在指定的超时后自动取消所有订单
	 * TODO if use 
	 */
	
	/**
	 * 关闭一个位置
	 * TODO if use 
	 */
	
}
