package com.thinkgem.jeesite.modules.platform.web.apiauthor;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.record.PageBreakRecord.Break;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.entity.account.BitMexAccount;
import com.thinkgem.jeesite.modules.platform.entity.account.BitOkAccount;
import com.thinkgem.jeesite.modules.platform.entity.apiauthor.APIAuthorize;
import com.thinkgem.jeesite.modules.platform.service.account.BitMexAccountService;
import com.thinkgem.jeesite.modules.platform.service.account.BitOkAccountService;
import com.thinkgem.jeesite.modules.platform.service.apiauthor.APIAuthorizeService;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexAccountInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.AccountInterfaceService;
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
	
	@Autowired
	private MexAccountInterfaceService mexAccountInterfaceService;
	
	@Autowired
	private AccountInterfaceService accountInterfaceService;
	
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
		if(StringUtils.isBlank(author.getId())){
			// 新增 ： 验证是否已存在同平台的API授权
			boolean flage = checkAPI(author.getPlatform());
			if(flage){
				addMessage(redirectAttributes, "该["+author.getPlatform()+"]平台API授权已存在，请勿重复授权。");
				return "redirect:"+Global.getAdminPath()+"/platform/apiauthor/aPIAuthorize/?repage";
			}
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
	
	@RequiresPermissions("platform:apiauthor:aPIAuthorize:edit")
	@RequestMapping(value = "getInfo")
	public String getInfo(APIAuthorize author, Model model, RedirectAttributes redirectAttributes) throws Exception {
		if (!beanValidator(model, author)){
			return form(author, model);
		}
		String msg = "";
		String platform = author.getPlatform();
		BigDecimal balance = new BigDecimal(0);
		if("okex".equals(platform)){
			balance = accountInterfaceService.getAccountbalance();
			if(null != balance){
				msg = "账户余额为："+balance+"BTC";
			}else{
				msg = "查询结果为空，请检查API是否正确。";
			}
		}else if("bitmex".equals(platform)){
			balance = mexAccountInterfaceService.getAccountbalance();
			if(null != balance){
				msg = "账户余额为："+balance+"XBT";
			}else{
				msg = "查询结果为空，请检查API是否正确。";
			}
		}else{
			addMessage(redirectAttributes, "查询["+platform+"]平台不存在！");
			return "redirect:"+Global.getAdminPath()+"/platform/apiauthor/aPIAuthorize/?repage";
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/platform/apiauthor/aPIAuthorize/?repage";
	}
	
	// 验证是否已存在同平台的API授权
	private Boolean checkAPI(String platform){
		boolean flage = false;
		APIAuthorize author = new APIAuthorize();
		User user = UserUtils.getUser();
		author.setUser(user);
		author.setPlatform(platform);
		List<APIAuthorize> list = aPIAuthorizeService.findList(author);
		if(null != list && !list.isEmpty()){
			flage = true;
		}
		return flage;
	}

	
}