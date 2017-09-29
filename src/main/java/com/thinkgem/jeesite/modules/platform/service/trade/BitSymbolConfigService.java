/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.service.trade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitSymbolConfig;
import com.thinkgem.jeesite.modules.platform.dao.trade.BitSymbolConfigDao;

/**
 * 币种参数配置Service
 * @author hzf
 * @version 2017-09-06
 */
@Service
@Transactional(readOnly = true)
public class BitSymbolConfigService extends CrudService<BitSymbolConfigDao, BitSymbolConfig> {

	public BitSymbolConfig get(String id) {
		return super.get(id);
	}
	
	public List<BitSymbolConfig> findList(BitSymbolConfig bitSymbolConfig) {
		return super.findList(bitSymbolConfig);
	}
	
	public Page<BitSymbolConfig> findPage(Page<BitSymbolConfig> page, BitSymbolConfig bitSymbolConfig) {
		return super.findPage(page, bitSymbolConfig);
	}
	
	@Transactional(readOnly = false)
	public void save(BitSymbolConfig bitSymbolConfig) {
		super.save(bitSymbolConfig);
	}
	
	@Transactional(readOnly = false)
	public void delete(BitSymbolConfig bitSymbolConfig) {
		super.delete(bitSymbolConfig);
	}
	
}