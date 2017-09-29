package com.thinkgem.jeesite.modules.platform.service.trade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitMonitor;
import com.thinkgem.jeesite.modules.platform.dao.trade.BitMonitorDao;

/**
 * 监控管理Service
 * @author hzf
 * @version 2017-08-27
 */
@Service
@Transactional(readOnly = true)
public class BitMonitorService extends CrudService<BitMonitorDao, BitMonitor> {

	public BitMonitor getByCode(String code) {
		return dao.getByCode(code);
	}
	
	public BitMonitor get(String id) {
		return super.get(id);
	}
	
	public List<BitMonitor> findList(BitMonitor bitMonitor) {
		return super.findList(bitMonitor);
	}
	
	public Page<BitMonitor> findPage(Page<BitMonitor> page, BitMonitor bitMonitor) {
		return super.findPage(page, bitMonitor);
	}
	
	@Transactional(readOnly = false)
	public void save(BitMonitor bitMonitor) {
		super.save(bitMonitor);
	}
	
	@Transactional(readOnly = false)
	public void delete(BitMonitor bitMonitor) {
		super.delete(bitMonitor);
	}
	
}