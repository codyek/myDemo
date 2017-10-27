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

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.entity.account.BitOkAccount;
import com.thinkgem.jeesite.modules.platform.entity.account.UserinfoFix;
import com.thinkgem.jeesite.modules.platform.entity.account.UserinfoFix.Info;
import com.thinkgem.jeesite.modules.platform.service.account.BitOkAccountService;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexAccountInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.AccountInterfaceService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * Ok账户信息表Controller
 * @author hzf
 * @version 2017-09-09
 */
@Controller
@RequestMapping(value = "${adminPath}/platform/account/bitOkAccount")
public class BitOkAccountController extends BaseController {

	@Autowired
	private BitOkAccountService bitOkAccountService;
	
	@ModelAttribute
	public BitOkAccount get(@RequestParam(required=false) String id) {
		BitOkAccount entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bitOkAccountService.get(id);
		}
		if (entity == null){
			entity = new BitOkAccount();
		}
		return entity;
	}
	
	@RequiresPermissions("platform:account:bitOkAccount:view")
	@RequestMapping(value = {"list", ""})
	public String list(BitOkAccount bitOkAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BitOkAccount> page = bitOkAccountService.findPage(new Page<BitOkAccount>(request, response), bitOkAccount); 
		model.addAttribute("page", page);
		return "modules/platform/account/bitOkAccountList";
	}

	@RequiresPermissions("platform:account:bitOkAccount:view")
	@RequestMapping(value = "form")
	public String form(BitOkAccount bitOkAccount, Model model) {
		model.addAttribute("bitOkAccount", bitOkAccount);
		return "modules/platform/account/bitOkAccountForm";
	}

	@RequiresPermissions("platform:account:bitOkAccount:edit")
	@RequestMapping(value = "save")
	public String save(BitOkAccount bitOkAccount, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bitOkAccount)){
			return form(bitOkAccount, model);
		}
		bitOkAccountService.save(bitOkAccount);
		addMessage(redirectAttributes, "保存Ok账户信息表成功");
		return "redirect:"+Global.getAdminPath()+"/platform/account/bitOkAccount/?repage";
	}
	
	@RequiresPermissions("platform:account:bitOkAccount:edit")
	@RequestMapping(value = "delete")
	public String delete(BitOkAccount bitOkAccount, RedirectAttributes redirectAttributes) {
		bitOkAccountService.delete(bitOkAccount);
		addMessage(redirectAttributes, "删除Ok账户信息表成功");
		return "redirect:"+Global.getAdminPath()+"/platform/account/bitOkAccount/?repage";
	}

	@Autowired
	private AccountInterfaceService accountInterfaceService;
	
	@Autowired
	private MexAccountInterfaceService mexAccountInterfaceService;
	
	@RequestMapping(value = "saveOne", method=RequestMethod.GET)
	@ResponseBody
	public String saveOne(BitOkAccount bitOkAccount,String symbol, Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUser();
		String json = "okk";
		try {
			json = accountInterfaceService.future_userinfo();
			logger.info(">>　future_userinfo json＝"+json);
			json = accountInterfaceService.future_userinfo_4fix();
			logger.info(">>　future_userinfo_4fix json＝"+json);
			/*UserinfoFix uf = JSONObject.parseObject(json,UserinfoFix.class);
			if(null != uf){
				boolean f = uf.getResult();
				logger.info(">>　f＝"+f);
				Info in = uf.getInfo();
				bitOkAccount = new BitOkAccount();
				bitOkAccount.setUseId(user.getId());
				bitOkAccount.setSymbol("btc_usd");
				bitOkAccount.setBalance(in.getBtc().getBalance());
				bitOkAccountService.save(bitOkAccount);
				
				bitOkAccount = new BitOkAccount();
				bitOkAccount.setUseId(user.getId());
				bitOkAccount.setSymbol("ltc_usd");
				bitOkAccount.setBalance(in.getLtc().getBalance());
				bitOkAccountService.save(bitOkAccount);
			}*/
			
			json = mexAccountInterfaceService.get_user_margin(symbol);
			logger.info(">>  get_user_margin = "+json);
			json = mexAccountInterfaceService.get_user_wallet(symbol);
			logger.info(">>  get_user_wallet = "+json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping(value = "getAccount", method=RequestMethod.POST)
	@ResponseBody
	public BitOkAccount getAccount(String type){
		BitOkAccount bitOkAccount = new BitOkAccount();
		User user = UserUtils.getUser();
		bitOkAccount.setUseId(user.getId());
		bitOkAccount.setSymbol(type);
		logger.info(">> uid="+user.getId()+"  ,type="+type);
		List<BitOkAccount> ret = bitOkAccountService.findList(bitOkAccount);
		if(null != ret && !ret.isEmpty()){
			bitOkAccount = ret.get(0);
		}else{
			bitOkAccount.setBalance(new BigDecimal(0));
			bitOkAccount.setAccountBalance(new BigDecimal(0));
		}
		return bitOkAccount;
	}
			
}