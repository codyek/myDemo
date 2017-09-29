package com.thinkgem.jeesite.modules.platform.web.trade;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitMonitor;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTrade;
import com.thinkgem.jeesite.modules.platform.service.trade.BitMonitorService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 交易主表Controller
 * @author hzf
 * @version 2017-08-27
 */
@Controller
@RequestMapping(value = "${adminPath}/platform/trade/bitTrade")
public class BitTradeController extends BaseController {

	@Autowired
	private BitTradeService bitTradeService;
	
	@Autowired
	private BitMonitorService bitMonitorService;
	
	@ModelAttribute
	public BitTrade get(@RequestParam(required=false) String id) {
		BitTrade entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bitTradeService.get(id);
		}
		if (entity == null){
			entity = new BitTrade();
		}
		return entity;
	}
	
	@RequiresPermissions("platform:trade:bitTrade:view")
	@RequestMapping(value = {"list", ""})
	public String list(BitTrade bitTrade, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BitTrade> page = bitTradeService.findPage(new Page<BitTrade>(request, response), bitTrade); 
		model.addAttribute("page", page);
		return "modules/platform/trade/bitTradeList";
	}

	@RequiresPermissions("platform:trade:bitTrade:view")
	@RequestMapping(value = "form")
	public String form(BitTrade bitTrade, Model model) {
		model.addAttribute("bitTrade", bitTrade);
		return "modules/platform/trade/bitTradeForm";
	}

	@RequiresPermissions("platform:trade:bitTrade:edit")
	@RequestMapping(value = "save")
	public String save(BitTrade bitTrade, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bitTrade)){
			return form(bitTrade, model);
		}
		bitTradeService.save(bitTrade);
		addMessage(redirectAttributes, "保存交易主表成功");
		return "redirect:"+Global.getAdminPath()+"/platform/trade/bitTrade/?repage";
	}
	
	@RequiresPermissions("platform:trade:bitTrade:edit")
	@RequestMapping(value = "delete")
	public String delete(BitTrade bitTrade, RedirectAttributes redirectAttributes) {
		bitTradeService.delete(bitTrade);
		addMessage(redirectAttributes, "删除交易主表成功");
		return "redirect:"+Global.getAdminPath()+"/platform/trade/bitTrade/?repage";
	}

	/**
	 * 获取当前监控交易数据
	 * @return List<BitTrade>
	 */
	@RequestMapping(value = "getTradeList")
	@ResponseBody
	public List<BitTrade> getTradeList(){
		List<BitTrade> list = null;
		BitMonitor bitMonitor = new BitMonitor();
		User user = UserUtils.getUser();
		bitMonitor.setUser(user);
		bitMonitor.setStatusFlag(Constants.STATUS_RUN);
		// 调service
		List<BitMonitor> monitors = bitMonitorService.findList(bitMonitor);
		logger.info("monitors size = "+monitors.size());
		if(null != monitors && !monitors.isEmpty()){
			String monitorCode = monitors.get(0).getCode();
			BitTrade bitTrade = new BitTrade();
			bitTrade.setMonitorCode(monitorCode);
			list = bitTradeService.findList(bitTrade);
			logger.info("BitTrades size = "+list.size());
		}
		return list;
	}
	
}