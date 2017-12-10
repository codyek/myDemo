package com.thinkgem.jeesite.modules.platform.web.trade;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.HttpUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.platform.entity.inter.Interfaces;
import com.thinkgem.jeesite.modules.platform.entity.trade.TradeTaskReq;
import com.thinkgem.jeesite.modules.platform.service.okex.AccountInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.InfoInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.OrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.thread.HedgeTotalControlService;

/**
 * 交易中心 Controller
 * @author hzf
 * @version 2017-07-08
 */
@Controller
@RequestMapping(value = "${adminPath}/hedge")
public class HedgeController extends BaseController {

	@Autowired
	private InfoInterfaceService interService;
	
	@Autowired
	private AccountInterfaceService accountService;
	
	@Autowired
	private OrderInterfaceService orderService;
	
	@Autowired
	private HedgeTotalControlService hedgeTotalControlService;
	
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = {"index", ""})
	public String indexPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		//model.addAttribute("page", page);
		return "modules/platform/trade/index";
	}
	
	/**
	 *  okex BTC与Mex XBT永续 对冲任务页面 v1
	 */
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = {"xbtUsdPage", ""})
	public String btcAndXbtUsdPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		TradeTaskReq req = new TradeTaskReq();
		model.addAttribute("TradeTaskReq", req);
		return "modules/platform/hedge/xbtusd";
	}
	
	/**
	 *  okex BTC与Mex XBT永续 对冲任务页面 v2
	 */
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = {"btcAndXbtusdPage", ""})
	public String btcAndXbtusdPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		TradeTaskReq req = new TradeTaskReq();
		model.addAttribute("TradeTaskReq", req);
		return "modules/platform/hedge/btcAndxbtusd";
	}
	
	/**
	 *  okex BTC与Mex XBT季度 对冲任务页面 v2
	 */
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = {"btcAndXbtqaePage", ""})
	public String btcAndXbtqaePage(HttpServletRequest request, HttpServletResponse response, Model model) {
		TradeTaskReq req = new TradeTaskReq();
		model.addAttribute("TradeTaskReq", req);
		return "modules/platform/hedge/btcAndxbtqae";
	}
	
	@RequiresPermissions("platform:trade:view")
	@RequestMapping(value = {"testPage", ""})
	public String testPage(TradeTaskReq req, Model model) {
		model.addAttribute("TradeTaskReq", req);
		return "modules/platform/trade/TestTradeForm";
	}
	
	/**
	* @Title: startJob
	* @param  maxAgio 最大差价（开仓差价）
	* @param  minAgio 最小差价（平仓差价）
	* @param  lever 杠杆
	* @param   保证金率
	* @return String
	* @throws
	 */
	@RequestMapping(value = "startJob")
	@ResponseBody
	public String startJob(TradeTaskReq req){
		// 入参验证
		if(null == req
			|| StringUtils.isBlank(req.getSymbolA())
			|| StringUtils.isBlank(req.getSymbolB())
			|| StringUtils.isBlank(req.getType())
			|| null == req.getDepositA()
			|| null == req.getDepositB()
			|| null == req.getLeverA()
			|| null == req.getLeverB()
			|| null == req.getMaxAgio()
			|| null == req.getMinAgio()
			){
			return "fail input check";
		}
		// 调service
		String msg = hedgeTotalControlService.control(req);
		
		return msg;
	}
	
	/**
	* @Title: updateData
	* @param  maxAgio 最大差价（开仓差价）
	* @param  minAgio 最小差价（平仓差价）
	* @param   保证金率
	* @return String
	 */
	@RequestMapping(value = "updateData")
	@ResponseBody
	public String updateData(TradeTaskReq req){
		// 入参验证
		if(null == req
			|| StringUtils.isBlank(req.getSymbolA())
			|| StringUtils.isBlank(req.getSymbolB())
			|| StringUtils.isBlank(req.getType())
			|| null == req.getDepositA()
			|| null == req.getDepositB()
			|| null == req.getLeverA()
			|| null == req.getLeverB()
			|| null == req.getMaxAgio()
			|| null == req.getMinAgio()
			){
			return "fail input check";
		}
		// 调service
		String msg = hedgeTotalControlService.updateData(req);
		return msg;
	}
			
	/**
	* @Title: stopJob
	* @return String
	 */
	@RequestMapping(value = "stopJob")
	@ResponseBody
	public String stopJob(String symbolA, String symbolB){
		// 调service
		String msg = hedgeTotalControlService.stopJob(symbolA,symbolB);
		return msg;
	}
}