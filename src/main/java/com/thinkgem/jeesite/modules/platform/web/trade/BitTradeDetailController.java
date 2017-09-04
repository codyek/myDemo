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
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTradeDetail;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeDetailService;

/**
 * 交易明细信息Controller
 * @author hzf
 * @version 2017-08-27
 */
@Controller
@RequestMapping(value = "${adminPath}/platform/trade/bitTradeDetail")
public class BitTradeDetailController extends BaseController {

	@Autowired
	private BitTradeDetailService bitTradeDetailService;
	
	@ModelAttribute
	public BitTradeDetail get(@RequestParam(required=false) String id) {
		BitTradeDetail entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bitTradeDetailService.get(id);
		}
		if (entity == null){
			entity = new BitTradeDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("platform:trade:bitTradeDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(BitTradeDetail bitTradeDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BitTradeDetail> page = bitTradeDetailService.findPage(new Page<BitTradeDetail>(request, response), bitTradeDetail); 
		model.addAttribute("page", page);
		return "modules/platform/trade/bitTradeDetailList";
	}

	@RequiresPermissions("platform:trade:bitTradeDetail:view")
	@RequestMapping(value = "form")
	public String form(BitTradeDetail bitTradeDetail, Model model) {
		model.addAttribute("bitTradeDetail", bitTradeDetail);
		return "modules/platform/trade/bitTradeDetailForm";
	}

	@RequiresPermissions("platform:trade:bitTradeDetail:edit")
	@RequestMapping(value = "save")
	public String save(BitTradeDetail bitTradeDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bitTradeDetail)){
			return form(bitTradeDetail, model);
		}
		bitTradeDetailService.save(bitTradeDetail);
		addMessage(redirectAttributes, "保存交易明细信息成功");
		return "redirect:"+Global.getAdminPath()+"/platform/trade/bitTradeDetail/?repage";
	}
	
	@RequiresPermissions("platform:trade:bitTradeDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(BitTradeDetail bitTradeDetail, RedirectAttributes redirectAttributes) {
		bitTradeDetailService.delete(bitTradeDetail);
		addMessage(redirectAttributes, "删除交易明细信息成功");
		return "redirect:"+Global.getAdminPath()+"/platform/trade/bitTradeDetail/?repage";
	}

}