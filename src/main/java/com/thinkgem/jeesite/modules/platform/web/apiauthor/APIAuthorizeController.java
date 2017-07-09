package com.thinkgem.jeesite.modules.platform.web.apiauthor;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.entity.apiauthor.APIAuthorize;
import com.thinkgem.jeesite.modules.platform.service.apiauthor.APIAuthorizeService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * API授权Controller
 * @author hzf
 * @version 2017-07-08
 */
@Controller
@RequestMapping(value = "${adminPath}/platform/apiauthor/aPIAuthorize")
public class APIAuthorizeController extends BaseController {

	@Autowired
	private APIAuthorizeService aPIAuthorizeService;
	
	@ModelAttribute
	public APIAuthorize get(@RequestParam(required=false) String id) {
		APIAuthorize entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = aPIAuthorizeService.get(id);
			// 隐藏key
			String akey = entity.getAppkey();
			String skey = entity.getSecretkey();
			entity.setAppkey(StringUtils.hideKey(akey));
			entity.setSecretkey(StringUtils.hideKey(skey));
		}
		if (entity == null){
			entity = new APIAuthorize();
		}
		return entity;
	}
	
	@RequiresPermissions("platform:apiauthor:aPIAuthorize:view")
	@RequestMapping(value = {"list", ""})
	public String list(APIAuthorize author, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		author.setUser(user);
		Page<APIAuthorize> page = aPIAuthorizeService.findPage(new Page<APIAuthorize>(request, response), author); 
		List<APIAuthorize> list = page.getList();
		for (APIAuthorize api : list) {
			String akey = api.getAppkey();
			String skey = api.getSecretkey();
			api.setAppkey(StringUtils.hideKey(akey));
			api.setSecretkey(StringUtils.hideKey(skey));
		}
		model.addAttribute("page", page);
		model.addAttribute("author", author);
		return "modules/platform/apiauthor/aPIAuthorizeList";
	}

	@RequiresPermissions("platform:apiauthor:aPIAuthorize:view")
	@RequestMapping(value = "form")
	public String form(APIAuthorize author, Model model) {
		model.addAttribute("author", author);
		return "modules/platform/apiauthor/aPIAuthorizeForm";
	}

	@RequiresPermissions("platform:apiauthor:aPIAuthorize:edit")
	@RequestMapping(value = "save")
	public String save(APIAuthorize author, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, author)){
			return form(author, model);
		}
		aPIAuthorizeService.save(author);
		addMessage(redirectAttributes, "保存API授权成功");
		return "redirect:"+Global.getAdminPath()+"/platform/apiauthor/aPIAuthorize/?repage";
	}
	
	@RequiresPermissions("platform:apiauthor:aPIAuthorize:edit")
	@RequestMapping(value = "delete")
	public String delete(APIAuthorize author, RedirectAttributes redirectAttributes) {
		aPIAuthorizeService.delete(author);
		addMessage(redirectAttributes, "删除API授权成功");
		return "redirect:"+Global.getAdminPath()+"/platform/apiauthor/aPIAuthorize/?repage";
	}

}