package com.thinkgem.jeesite.modules.platform.service.trade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTrade;
import com.thinkgem.jeesite.modules.platform.dao.trade.BitTradeDao;

/**
 * 交易主表Service
 * @author hzf
 * @version 2017-08-27
 */
@Service
@Transactional(readOnly = true)
public class BitTradeService extends CrudService<BitTradeDao, BitTrade> {

	public BitTrade getByCode(String code) {
		return dao.getByCode(code);
	}
	
	public BitTrade get(String id) {
		return super.get(id);
	}
	
	public List<BitTrade> findList(BitTrade bitTrade) {
		return super.findList(bitTrade);
	}
	
	public Page<BitTrade> findPage(Page<BitTrade> page, BitTrade bitTrade) {
		return super.findPage(page, bitTrade);
	}
	
	@Transactional(readOnly = false)
	public void save(BitTrade bitTrade) {
		super.save(bitTrade);
	}
	
	@Transactional(readOnly = false)
	public void delete(BitTrade bitTrade) {
		super.delete(bitTrade);
	}
	
}