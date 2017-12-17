/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.sms.entity.BitSms;
import com.thinkgem.jeesite.modules.sms.dao.BitSmsDao;

/**
 * 短信发送Service
 * @author hzf
 * @version 2017-12-17
 */
@Service
@Transactional(readOnly = true)
public class BitSmsService extends CrudService<BitSmsDao, BitSms> {

	public BitSms get(String id) {
		return super.get(id);
	}
	
	public List<BitSms> findList(BitSms bitSms) {
		return super.findList(bitSms);
	}
	
	public Page<BitSms> findPage(Page<BitSms> page, BitSms bitSms) {
		return super.findPage(page, bitSms);
	}
	
	@Transactional(readOnly = false)
	public void save(BitSms bitSms) {
		super.save(bitSms);
	}
	
	@Transactional(readOnly = false)
	public void delete(BitSms bitSms) {
		super.delete(bitSms);
	}
	
}