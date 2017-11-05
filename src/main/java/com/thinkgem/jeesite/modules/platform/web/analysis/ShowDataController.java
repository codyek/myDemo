package com.thinkgem.jeesite.modules.platform.web.analysis;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.mongodb.model.BtcToQaeTradeData;
import com.thinkgem.jeesite.modules.mongodb.model.BtcToXbtTradeData;
import com.thinkgem.jeesite.modules.mongodb.service.BtcQaeTradeDataInterface;
import com.thinkgem.jeesite.modules.mongodb.service.BtcTradeDataInterface;
import com.thinkgem.jeesite.modules.mongodb.service.MexLtcDepthDataInterface;
import com.thinkgem.jeesite.modules.mongodb.service.MexXbtQaeDepthDataInterface;
import com.thinkgem.jeesite.modules.mongodb.service.MexXbtUsdDepthDataInterface;
import com.thinkgem.jeesite.modules.mongodb.service.OkexBtcDepthDataInterface;
import com.thinkgem.jeesite.modules.mongodb.service.OkexLtcDepthDataInterface;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.entity.trade.TradeTaskReq;

@Controller
@RequestMapping(value = "${adminPath}/analysis")
public class ShowDataController extends BaseController {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private BtcTradeDataInterface btcTradeData;
	@Autowired
	private BtcQaeTradeDataInterface xbtQaeTradeData;
	
	@Autowired
	private OkexBtcDepthDataInterface OkexBtcDepth;
	@Autowired
	private OkexLtcDepthDataInterface OkexLtcDepth;
	@Autowired
	private MexXbtUsdDepthDataInterface MexXbtUsdDepth;
	@Autowired
	private MexXbtQaeDepthDataInterface MexXbtQaeDepth;
	@Autowired
	private MexLtcDepthDataInterface MexLtcDepth;
	
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = {"xbtUsdDataPage", ""})
	public String btcAndXbtUsdPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		TradeTaskReq req = new TradeTaskReq();
		model.addAttribute("TradeTaskReq", req);
		return "modules/platform/hedge/analysis/xbtusd";
	}
	
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = "getBtcXbtusdPriceData")
	@ResponseBody
	public JSONArray getBtcXbtusdPriceData(String startDt, String endDt){
		log.info(">>>-- postPrice BTC  input="+ startDt + " , " +endDt);
		if(null != startDt && null != endDt){
			long start = Long.parseLong(
					DateUtils.formatDate(
							DateUtils.parseDate(startDt), "yyyyMMddHHmmss"));
			long end = Long.parseLong(
					DateUtils.formatDate(
							DateUtils.parseDate(endDt), "yyyyMMddHHmmss"));
			log.info(">>>-- postPrice Btc  start="+ start + " , end=" +end);
			Direction direction = Direction.ASC;
			List<BtcToXbtTradeData> list = btcTradeData.findByTimeBetween(start, end,new Sort(direction,"time"));
			JSONArray arr = JSONArray.parseArray(JSON.toJSONString(list));
			return arr;
		}else{
			return null;
		}
	}
	
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = "getDepthData")
	@ResponseBody
	public JSONArray getDepthData(String symbol,String startDt, String endDt){
		log.info(">>>-- getDepthData  symbol="+ symbol+",time="+ startDt + " - " +endDt);
		if(StringUtils.isNoneBlank(symbol)){
			long start = Long.parseLong(
					DateUtils.formatDate(
							DateUtils.parseDate(startDt), "yyyyMMddHHmmss"));
			long end = Long.parseLong(
					DateUtils.formatDate(
							DateUtils.parseDate(endDt), "yyyyMMddHHmmss"));
			log.info(">>>-- getDepthData start="+ start + " , end=" +end);
			Direction direction = Direction.ASC;
			List list = null;
			if(Constants.SYMBOL_BTC_USD.equals(symbol)){
				list = OkexBtcDepth.findByTimeBetween(start, end,new Sort(direction,"time"));
			}else if(Constants.SYMBOL_LTC_USD.equals(symbol)){
				list = OkexLtcDepth.findByTimeBetween(start, end,new Sort(direction,"time"));
			}else if(Constants.SYMBOL_XBTUSD.equals(symbol)){
				list = MexXbtUsdDepth.findByTimeBetween(start, end,new Sort(direction,"time"));
			}else if(Constants.SYMBOL_XBTQAE.equals(symbol)){
				list = MexXbtQaeDepth.findByTimeBetween(start, end,new Sort(direction,"time"));
			}else if(Constants.SYMBOL_LTCQAE.equals(symbol)){
				list = MexLtcDepth.findByTimeBetween(start, end,new Sort(direction,"time"));
			}
			JSONArray arr = JSONArray.parseArray(JSON.toJSONString(list));
			return arr;
		}else{
			return null;
		}
	}
	
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = {"xbtQaeDataPage", ""})
	public String xbtQaeDataPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		TradeTaskReq req = new TradeTaskReq();
		model.addAttribute("TradeTaskReq", req);
		return "modules/platform/hedge/analysis/xbtqae";
	}
	
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = "getBtcXbtqaePriceData")
	@ResponseBody
	public JSONArray getBtcXbtqaePriceData(String startDt, String endDt){
		log.info(">>>-- postPrice Qae  input="+ startDt + " , " +endDt);
		if(null != startDt && null != endDt){
			long start = Long.parseLong(
					DateUtils.formatDate(
							DateUtils.parseDate(startDt), "yyyyMMddHHmmss"));
			long end = Long.parseLong(
					DateUtils.formatDate(
							DateUtils.parseDate(endDt), "yyyyMMddHHmmss"));
			log.info(">>>-- postPrice Qae  start="+ start + " , end=" +end);
			Direction direction = Direction.ASC;
			List<BtcToQaeTradeData> list = xbtQaeTradeData.findByTimeBetween(start, end,new Sort(direction,"time"));
			JSONArray arr = JSONArray.parseArray(JSON.toJSONString(list));
			return arr;
		}else{
			return null;
		}
	}
	
}
