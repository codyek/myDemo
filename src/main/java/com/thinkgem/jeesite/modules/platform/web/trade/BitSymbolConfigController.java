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
import com.thinkgem.jeesite.modules.platform.entity.trade.BitSymbolConfig;
import com.thinkgem.jeesite.modules.platform.service.trade.BitSymbolConfigService;

/**
 * 币种参数配置Controller
 * @author hzf
 * @version 2017-09-06
 */
@Controller
@RequestMapping(value = "${adminPath}/platform/trade/bitSymbolConfig")
public class BitSymbolConfigController extends BaseController {

	@Autowired
	private BitSymbolConfigService bitSymbolConfigService;
	
	@ModelAttribute
	public BitSymbolConfig get(@RequestParam(required=false) String id) {
		BitSymbolConfig entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bitSymbolConfigService.get(id);
		}
		if (entity == null){
			entity = new BitSymbolConfig();
		}
		return entity;
	}
	
	@RequiresPermissions("platform:trade:bitSymbolConfig:view")
	@RequestMapping(value = {"list", ""})
	public String list(BitSymbolConfig bitSymbolConfig, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BitSymbolConfig> page = bitSymbolConfigService.findPage(new Page<BitSymbolConfig>(request, response), bitSymbolConfig); 
		model.addAttribute("page", page);
		return "modules/platform/trade/bitSymbolConfigList";
	}

	@RequiresPermissions("platform:trade:bitSymbolConfig:view")
	@RequestMapping(value = "form")
	public String form(BitSymbolConfig bitSymbolConfig, Model model) {
		model.addAttribute("bitSymbolConfig", bitSymbolConfig);
		return "modules/platform/trade/bitSymbolConfigForm";
	}

	@RequiresPermissions("platform:trade:bitSymbolConfig:edit")
	@RequestMapping(value = "save")
	public String save(BitSymbolConfig bitSymbolConfig, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bitSymbolConfig)){
			return form(bitSymbolConfig, model);
		}
		bitSymbolConfigService.save(bitSymbolConfig);
		addMessage(redirectAttributes, "保存币种参数配置成功");
		return "redirect:"+Global.getAdminPath()+"/platform/trade/bitSymbolConfig/?repage";
	}
	
	@RequiresPermissions("platform:trade:bitSymbolConfig:edit")
	@RequestMapping(value = "delete")
	public String delete(BitSymbolConfig bitSymbolConfig, RedirectAttributes redirectAttributes) {
		bitSymbolConfigService.delete(bitSymbolConfig);
		addMessage(redirectAttributes, "删除币种参数配置成功");
		return "redirect:"+Global.getAdminPath()+"/platform/trade/bitSymbolConfig/?repage";
	}

}