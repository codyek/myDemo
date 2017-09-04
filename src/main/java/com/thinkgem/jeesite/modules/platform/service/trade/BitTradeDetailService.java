package com.thinkgem.jeesite.modules.platform.service.trade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTradeDetail;
import com.thinkgem.jeesite.modules.platform.dao.trade.BitTradeDetailDao;

/**
 * 交易明细信息Service
 * @author hzf
 * @version 2017-08-27
 */
@Service
@Transactional(readOnly = true)
public class BitTradeDetailService extends CrudService<BitTradeDetailDao, BitTradeDetail> {

	public BitTradeDetail getByCode(String code) {
		return dao.getByCode(code);
	}
	
	public BitTradeDetail get(String id) {
		return super.get(id);
	}
	
	public List<BitTradeDetail> findList(BitTradeDetail bitTradeDetail) {
		return super.findList(bitTradeDetail);
	}
	
	public Page<BitTradeDetail> findPage(Page<BitTradeDetail> page, BitTradeDetail bitTradeDetail) {
		return super.findPage(page, bitTradeDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(BitTradeDetail bitTradeDetail) {
		super.save(bitTradeDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(BitTradeDetail bitTradeDetail) {
		super.delete(bitTradeDetail);
	}
	
}