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
import com.thinkgem.jeesite.modules.platform.service.trade.BitMonitorService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 监控管理Controller
 * @author hzf
 * @version 2017-08-27
 */
@Controller
@RequestMapping(value = "${adminPath}/platform/trade/bitMonitor")
public class BitMonitorController extends BaseController {

	@Autowired
	private BitMonitorService bitMonitorService;
	
	@ModelAttribute
	public BitMonitor get(@RequestParam(required=false) String id) {
		BitMonitor entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bitMonitorService.get(id);
		}
		if (entity == null){
			entity = new BitMonitor();
		}
		return entity;
	}
	
	@RequiresPermissions("platform:trade:bitMonitor:view")
	@RequestMapping(value = {"list", ""})
	public String list(BitMonitor bitMonitor, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BitMonitor> page = bitMonitorService.findPage(new Page<BitMonitor>(request, response), bitMonitor); 
		model.addAttribute("page", page);
		return "modules/platform/trade/bitMonitorList";
	}

	@RequiresPermissions("platform:trade:bitMonitor:view")
	@RequestMapping(value = "form")
	public String form(BitMonitor bitMonitor, Model model) {
		model.addAttribute("bitMonitor", bitMonitor);
		return "modules/platform/trade/bitMonitorForm";
	}

	@RequiresPermissions("platform:trade:bitMonitor:edit")
	@RequestMapping(value = "save")
	public String save(BitMonitor bitMonitor, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bitMonitor)){
			return form(bitMonitor, model);
		}
		bitMonitorService.save(bitMonitor);
		addMessage(redirectAttributes, "保存监控管理成功");
		return "redirect:"+Global.getAdminPath()+"/platform/trade/bitMonitor/?repage";
	}
	
	@RequiresPermissions("platform:trade:bitMonitor:edit")
	@RequestMapping(value = "delete")
	public String delete(BitMonitor bitMonitor, RedirectAttributes redirectAttributes) {
		bitMonitorService.delete(bitMonitor);
		addMessage(redirectAttributes, "删除监控管理成功");
		return "redirect:"+Global.getAdminPath()+"/platform/trade/bitMonitor/?repage";
	}

	/**
	* @return String
	 */
	@RequestMapping(value = "getStatus")
	@ResponseBody
	public String getStatus(){
		String msg = "success";
		BitMonitor bitMonitor = new BitMonitor();
		User user = UserUtils.getUser();
		bitMonitor.setUser(user);
		bitMonitor.setStatusFlag(Constants.STATUS_RUN);
		// 调service
		List<BitMonitor> list = bitMonitorService.findList(bitMonitor);
		logger.info("list size = "+list.size());
		if(null != list && !list.isEmpty()){
			msg = "running";
		}
		return msg;
	}
}