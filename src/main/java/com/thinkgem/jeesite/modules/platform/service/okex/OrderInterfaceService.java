package com.thinkgem.jeesite.modules.platform.service.okex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.inter.OkexInterConstants;
import com.thinkgem.jeesite.modules.platform.entity.order.OkexOrder;
import com.thinkgem.jeesite.modules.platform.service.OkexBaseService;


/**
 * 
* @Description: 订单接口服务
* @author EK huangone 
* @date 2017-7-9 上午12:38:50 
*
 */
@Service
@Transactional(readOnly = true)
public class OrderInterfaceService extends OkexBaseService{

	/**
	* 合约下单
	* @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
    * @param price 价格
    * @param amount 委托数量
    * @param type 1:开多   2:开空   3:平多   4:平空
    * @param match_price 是否为对手价 0:不是    1:是   ,当取值为1时,price无效
    * @param lever_rate 杠杆倍数 value:10\20 默认10
	* @return String
	* @throws Exception
	 */
	public String future_trade(String symbol, String contractType,
			String price, String amount, String type, String match_price,
			String lever_rate) throws Exception {
		String url = OkexInterConstants.PFUTURE_TRADE_URL;
		Map<String,String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("contract_type", contractType);
		params.put("price", price);
		params.put("amount", amount);
		params.put("type", type);
		params.put("match_price", match_price);
		params.put("lever_rate", lever_rate);
		return doPost(url, params);
	}
	
	/**
	 * 批量下单
	 * @param symbol
	 *            btc_usd：比特币， ltc_usd：莱特币
	 * @param contractType
	 *            合约类型。this_week：当周；next_week：下周；quarter：季度
	 * @param orders_data
	 *            JSON类型的字符串
	 *            例：[{price:5,amount:2,type:1,match_price:1},{price:2,
	 *            amount:3,type:1,match_price:1}]
	 *            最大下单量为5，price,amount,type,match_price参数参考future_trade接口中的说明
	 * @param lever_rate
	 *            btc_usd：比特币， ltc_usd：莱特币
	 * @return String
	 * @throws Exception
	 */
	public String future_batch_trade(String symbol, String contractType,
			String orders_data, String lever_rate) throws Exception {
		String url = OkexInterConstants.PFUTURE_BATCH_TRADE_URL;
		Map<String,String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("contract_type", contractType);
		params.put("orders_data", orders_data);
		params.put("lever_rate", lever_rate);
		return doPost(url, params);
	}
	
	/**
	* 取消合约订单
	* @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
    * @param order_id 订单ID(多个订单ID中间以","分隔,一次最多允许撤消3个订单)
	* @return String
	* @throws Exception
	 */
	public String future_cancel(String symbol, String contractType, String order_id) throws Exception{
		String url = OkexInterConstants.PFUTURE_CANCEL_URL;
		Map<String,String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("contract_type", contractType);
		params.put("order_id", order_id);
		return doPost(url, params);
	}
	
	/**
	* 获取合约订单信息
	* @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
    * @param order_id 订单ID -1:查询指定状态的订单，否则查询相应订单号的订单
    * @param status 查询状态 1:未完成的订单 2:已经完成的订单
    * @param current_page 当前页数
    * @param page_length 每页获取条数，最多不超过50
	* @return String
	* @throws Exception
	 */
	public String future_order_info(String symbol, String contractType,
			String order_id, String status, String current_page, String page_length) throws Exception {
		String url = OkexInterConstants.PFUTURE_ORDER_INFO_URL;
		Map<String,String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("contract_type", contractType);
		params.put("order_id", order_id);
		params.put("status", status);
		params.put("current_page", current_page);
		params.put("page_length", page_length);
		return doPost(url, params);
	}
	
	/**
	* 获取合约订单信息
	* @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
    * @param order_id 订单ID -1:查询指定状态的订单，否则查询相应订单号的订单
    * @param status 查询状态 1:未完成的订单 2:已经完成的订单
    * @param current_page 当前页数
    * @param page_length 每页获取条数，最多不超过50
	* @return String
	* @throws Exception
	 */
	public List<OkexOrder> getOrderInfo(String symbol, String contractType,
			String order_id, String status, String current_page, String page_length) throws Exception {
		List<OkexOrder> orderInfos = null;
		String json = future_order_info(symbol,contractType,order_id,status,current_page,page_length);
		if(StringUtils.isNotBlank(json)){
			JSONObject jobJ = JSONObject.parseObject(json);
			if(jobJ.containsKey("result") && jobJ.containsKey("orders") && jobJ.getBooleanValue("result")){
				JSONArray arrJs = jobJ.getJSONArray("orders");
				orderInfos = new ArrayList<OkexOrder>();
				for (int i = 0; i < arrJs.size(); i++) {
					JSONObject js = arrJs.getJSONObject(i);
					OkexOrder order = new OkexOrder();
					order.setOrderId(js.getString("order_id"));
					order.setSymbol(js.getString("symbol"));
					order.setType(js.getString("type"));
					order.setTypeString(OkexOrder.getTypeStr(js.getString("type")));
					order.setPriceAvg(js.getBigDecimal("price_avg"));
					order.setDealAmount(js.getInteger("deal_amount"));
					order.setFee(js.getBigDecimal("fee"));
					order.setStatus(OkexOrder.getStatusStr(js.getString("status")));
					Long time = js.getLong("create_date");
					if(null != time){
						order.setCreateDateLong(time);
						order.setCreateDate(DateUtils.stampToDate(time));
					}
					orderInfos.add(order);
				}
			}
		}
		
		return orderInfos;
	}
	
	/**
	* 批量获取合约订单信息
	* @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
    * @param order_id 订单ID(多个订单ID中间以","分隔,一次最多允许查询50个订单)
	* @return String
	* @throws Exception
	 */
	public String future_orders_info(String symbol, String contractType, String order_id) throws Exception{
		String url = OkexInterConstants.PFUTURE_ORDERS_INFO_URL;
		Map<String,String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("contract_type", contractType);
		params.put("order_id", order_id);
		return doPost(url, params);
	}
	
	/**
	* 获取合约爆仓单
	* @param symbol btc_usd：比特币， ltc_usd：莱特币
    * @param contractType 合约类型。this_week：当周；next_week：下周；quarter：季度
    * @param status 状态 0：最近7天未成交 1:最近7天已成交
    * @param current_page 当前页数索引值
    * @param page_number 当前页数(使用page_number时current_page失效，current_page无需传)
    * @param page_length 每页获取条数，最多不超过50
	* @return String
	* @throws Exception
	 */
	public String future_explosive(String symbol, String contractType,
			String status, Integer current_page, Integer page_number,
			Integer page_length) throws Exception {
		
		String url = OkexInterConstants.PFUTURE_EXPLOSIVE_URL;
		Map<String,String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("contract_type", contractType);
		params.put("status", contractType);
		params.put("current_page", String.valueOf(current_page));
		params.put("page_number", String.valueOf(page_number));
		params.put("page_length", String.valueOf(page_length));
		
		return doPost(url, params);
	}
}
