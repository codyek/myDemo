/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.web.trade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTrade;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeService;

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

}