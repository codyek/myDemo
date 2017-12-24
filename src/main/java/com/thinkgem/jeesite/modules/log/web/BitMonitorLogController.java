/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.log.web;

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
import com.thinkgem.jeesite.modules.log.entity.BitMonitorLog;
import com.thinkgem.jeesite.modules.log.service.BitMonitorLogService;

/**
 * 监控记录表Controller
 * @author hzf
 * @version 2017-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/log/bitMonitorLog")
public class BitMonitorLogController extends BaseController {

	@Autowired
	private BitMonitorLogService bitMonitorLogService;
	
	@ModelAttribute
	public BitMonitorLog get(@RequestParam(required=false) String id) {
		BitMonitorLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bitMonitorLogService.get(id);
		}
		if (entity == null){
			entity = new BitMonitorLog();
		}
		return entity;
	}
	
	@RequiresPermissions("log:bitMonitorLog:view")
	@RequestMapping(value = {"list", ""})
	public String list(BitMonitorLog bitMonitorLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BitMonitorLog> page = bitMonitorLogService.findPage(new Page<BitMonitorLog>(request, response), bitMonitorLog); 
		model.addAttribute("page", page);
		return "modules/log/bitMonitorLogList";
	}

	@RequiresPermissions("log:bitMonitorLog:view")
	@RequestMapping(value = "form")
	public String form(BitMonitorLog bitMonitorLog, Model model) {
		model.addAttribute("bitMonitorLog", bitMonitorLog);
		return "modules/log/bitMonitorLogForm";
	}

	@RequiresPermissions("log:bitMonitorLog:edit")
	@RequestMapping(value = "save")
	public String save(BitMonitorLog bitMonitorLog, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bitMonitorLog)){
			return form(bitMonitorLog, model);
		}
		bitMonitorLogService.save(bitMonitorLog);
		addMessage(redirectAttributes, "保存监控记录表成功");
		return "redirect:"+Global.getAdminPath()+"/log/bitMonitorLog/?repage";
	}
	
	@RequiresPermissions("log:bitMonitorLog:edit")
	@RequestMapping(value = "delete")
	public String delete(BitMonitorLog bitMonitorLog, RedirectAttributes redirectAttributes) {
		bitMonitorLogService.delete(bitMonitorLog);
		addMessage(redirectAttributes, "删除监控记录表成功");
		return "redirect:"+Global.getAdminPath()+"/log/bitMonitorLog/?repage";
	}

}