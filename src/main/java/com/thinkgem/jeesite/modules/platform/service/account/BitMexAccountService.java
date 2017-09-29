/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.service.account;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.platform.entity.account.BitMexAccount;
import com.thinkgem.jeesite.modules.platform.dao.account.BitMexAccountDao;

/**
 * mex 账户信息表Service
 * @author hzf
 * @version 2017-09-13
 */
@Service
@Transactional(readOnly = true)
public class BitMexAccountService extends CrudService<BitMexAccountDao, BitMexAccount> {

	public BitMexAccount get(String id) {
		return super.get(id);
	}
	
	public List<BitMexAccount> findList(BitMexAccount bitMexAccount) {
		return super.findList(bitMexAccount);
	}
	
	public Page<BitMexAccount> findPage(Page<BitMexAccount> page, BitMexAccount bitMexAccount) {
		return super.findPage(page, bitMexAccount);
	}
	
	@Transactional(readOnly = false)
	public void save(BitMexAccount bitMexAccount) {
		super.save(bitMexAccount);
	}
	
	@Transactional(readOnly = false)
	public void delete(BitMexAccount bitMexAccount) {
		super.delete(bitMexAccount);
	}
	
}