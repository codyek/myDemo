/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sms.web;

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
import com.thinkgem.jeesite.modules.sms.entity.BitSms;
import com.thinkgem.jeesite.modules.sms.service.BitSmsService;

/**
 * 短信发送Controller
 * @author hzf
 * @version 2017-12-17
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/bitSms")
public class BitSmsController extends BaseController {

	@Autowired
	private BitSmsService bitSmsService;
	
	@ModelAttribute
	public BitSms get(@RequestParam(required=false) String id) {
		BitSms entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bitSmsService.get(id);
		}
		if (entity == null){
			entity = new BitSms();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:bitSms:view")
	@RequestMapping(value = {"list", ""})
	public String list(BitSms bitSms, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BitSms> page = bitSmsService.findPage(new Page<BitSms>(request, response), bitSms); 
		model.addAttribute("page", page);
		return "modules/sms/bitSmsList";
	}

	@RequiresPermissions("sms:bitSms:view")
	@RequestMapping(value = "form")
	public String form(BitSms bitSms, Model model) {
		model.addAttribute("bitSms", bitSms);
		return "modules/sms/bitSmsForm";
	}

	@RequiresPermissions("sms:bitSms:edit")
	@RequestMapping(value = "save")
	public String save(BitSms bitSms, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bitSms)){
			return form(bitSms, model);
		}
		bitSmsService.save(bitSms);
		addMessage(redirectAttributes, "保存短信发送成功");
		return "redirect:"+Global.getAdminPath()+"/sms/bitSms/?repage";
	}
	
	@RequiresPermissions("sms:bitSms:edit")
	@RequestMapping(value = "delete")
	public String delete(BitSms bitSms, RedirectAttributes redirectAttributes) {
		bitSmsService.delete(bitSms);
		addMessage(redirectAttributes, "删除短信发送成功");
		return "redirect:"+Global.getAdminPath()+"/sms/bitSms/?repage";
	}

}