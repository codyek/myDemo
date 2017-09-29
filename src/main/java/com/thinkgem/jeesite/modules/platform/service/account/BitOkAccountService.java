/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.service.account;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.platform.entity.account.BitOkAccount;
import com.thinkgem.jeesite.modules.platform.dao.account.BitOkAccountDao;

/**
 * Ok账户信息表Service
 * @author hzf
 * @version 2017-09-09
 */
@Service
@Transactional(readOnly = true)
public class BitOkAccountService extends CrudService<BitOkAccountDao, BitOkAccount> {

	public BitOkAccount get(String id) {
		return super.get(id);
	}
	
	public List<BitOkAccount> findList(BitOkAccount bitOkAccount) {
		return super.findList(bitOkAccount);
	}
	
	public Page<BitOkAccount> findPage(Page<BitOkAccount> page, BitOkAccount bitOkAccount) {
		return super.findPage(page, bitOkAccount);
	}
	
	@Transactional(readOnly = false)
	public void save(BitOkAccount bitOkAccount) {
		super.save(bitOkAccount);
	}
	
	@Transactional(readOnly = false)
	public void delete(BitOkAccount bitOkAccount) {
		super.delete(bitOkAccount);
	}
	
}