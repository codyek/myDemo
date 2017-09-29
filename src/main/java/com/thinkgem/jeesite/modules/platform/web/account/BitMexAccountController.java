package com.thinkgem.jeesite.modules.platform.web.account;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.entity.account.BitMexAccount;
import com.thinkgem.jeesite.modules.platform.entity.account.BitOkAccount;
import com.thinkgem.jeesite.modules.platform.service.account.BitMexAccountService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * mex 账户信息表Controller
 * @author hzf
 * @version 2017-09-13
 */
@Controller
@RequestMapping(value = "${adminPath}/platform/account/bitMexAccount")
public class BitMexAccountController extends BaseController {

	@Autowired
	private BitMexAccountService bitMexAccountService;
	
	@ModelAttribute
	public BitMexAccount get(@RequestParam(required=false) String id) {
		BitMexAccount entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bitMexAccountService.get(id);
		}
		if (entity == null){
			entity = new BitMexAccount();
		}
		return entity;
	}
	
	@RequiresPermissions("platform:account:bitMexAccount:view")
	@RequestMapping(value = {"list", ""})
	public String list(BitMexAccount bitMexAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BitMexAccount> page = bitMexAccountService.findPage(new Page<BitMexAccount>(request, response), bitMexAccount); 
		model.addAttribute("page", page);
		return "modules/platform/account/bitMexAccountList";
	}

	@RequiresPermissions("platform:account:bitMexAccount:view")
	@RequestMapping(value = "form")
	public String form(BitMexAccount bitMexAccount, Model model) {
		model.addAttribute("bitMexAccount", bitMexAccount);
		return "modules/platform/account/bitMexAccountForm";
	}

	@RequiresPermissions("platform:account:bitMexAccount:edit")
	@RequestMapping(value = "save")
	public String save(BitMexAccount bitMexAccount, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bitMexAccount)){
			return form(bitMexAccount, model);
		}
		bitMexAccountService.save(bitMexAccount);
		addMessage(redirectAttributes, "保存mex 账户信息表成功");
		return "redirect:"+Global.getAdminPath()+"/platform/account/bitMexAccount/?repage";
	}
	
	@RequiresPermissions("platform:account:bitMexAccount:edit")
	@RequestMapping(value = "delete")
	public String delete(BitMexAccount bitMexAccount, RedirectAttributes redirectAttributes) {
		bitMexAccountService.delete(bitMexAccount);
		addMessage(redirectAttributes, "删除mex 账户信息表成功");
		return "redirect:"+Global.getAdminPath()+"/platform/account/bitMexAccount/?repage";
	}
	
	@RequestMapping(value = "getAccount", method=RequestMethod.POST)
	@ResponseBody
	public BitMexAccount getAccount(String type){
		BitMexAccount bitMexAccount = new BitMexAccount();
		User user = UserUtils.getUser();
		bitMexAccount.setUseId(user.getId());
		bitMexAccount.setSymbol(type);
		logger.info(">> uid="+user.getId()+"  ,type="+type);
		List<BitMexAccount> ret = bitMexAccountService.findList(bitMexAccount);
		if(null != ret && !ret.isEmpty()){
			bitMexAccount = ret.get(0);
		}else{
			bitMexAccount.setBalance(new BigDecimal(0));
			bitMexAccount.setAccountBalance(new BigDecimal(0));
		}
		return bitMexAccount;
	}

}