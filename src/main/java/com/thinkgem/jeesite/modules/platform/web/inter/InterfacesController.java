package com.thinkgem.jeesite.modules.platform.web.inter;

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
import com.thinkgem.jeesite.modules.platform.entity.inter.Interfaces;
import com.thinkgem.jeesite.modules.platform.service.inter.InterfacesService;

/**
 * 各平台接口信息Controller
 * @author hzf
 * @version 2017-07-06
 */
@Controller
@RequestMapping(value = "${adminPath}/platform/inter/interfaces")
public class InterfacesController extends BaseController {

	@Autowired
	private InterfacesService interfacesService;
	
	@ModelAttribute
	public Interfaces get(@RequestParam(required=false) String id) {
		Interfaces entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = interfacesService.get(id);
		}
		if (entity == null){
			entity = new Interfaces();
		}
		return entity;
	}
	
	@RequiresPermissions("platform:inter:interfaces:view")
	@RequestMapping(value = {"list", ""})
	public String list(Interfaces interfaces, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Interfaces> page = interfacesService.findPage(new Page<Interfaces>(request, response), interfaces); 
		model.addAttribute("page", page);
		return "modules/platform/inter/interfacesList";
	}

	@RequiresPermissions("platform:inter:interfaces:view")
	@RequestMapping(value = "form")
	public String form(Interfaces interfaces, Model model) {
		model.addAttribute("interfaces", interfaces);
		return "modules/platform/inter/interfacesForm";
	}

	@RequiresPermissions("platform:inter:interfaces:edit")
	@RequestMapping(value = "save")
	public String save(Interfaces interfaces, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, interfaces)){
			return form(interfaces, model);
		}
		interfacesService.save(interfaces);
		addMessage(redirectAttributes, "保存接口信息成功");
		return "redirect:"+Global.getAdminPath()+"/platform/inter/interfaces/?repage";
	}
	
	@RequiresPermissions("platform:inter:interfaces:edit")
	@RequestMapping(value = "delete")
	public String delete(Interfaces interfaces, RedirectAttributes redirectAttributes) {
		interfacesService.delete(interfaces);
		addMessage(redirectAttributes, "删除接口信息成功");
		return "redirect:"+Global.getAdminPath()+"/platform/inter/interfaces/?repage";
	}

}