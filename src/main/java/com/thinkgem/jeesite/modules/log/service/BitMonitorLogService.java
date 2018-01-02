package com.thinkgem.jeesite.modules.log.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.log.entity.BitMonitorLog;
import com.thinkgem.jeesite.modules.log.dao.BitMonitorLogDao;

/**
 * 监控记录表Service
 * @author hzf
 * @version 2017-12-23
 */
@Service
@Transactional(readOnly = true)
public class BitMonitorLogService extends CrudService<BitMonitorLogDao, BitMonitorLog> {

	public List<BitMonitorLog> findAllSendList(){
		return dao.findAllSendList();
	}
	
	public BitMonitorLog get(String id) {
		return super.get(id);
	}
	
	public List<BitMonitorLog> findList(BitMonitorLog bitMonitorLog) {
		return super.findList(bitMonitorLog);
	}
	
	public Page<BitMonitorLog> findPage(Page<BitMonitorLog> page, BitMonitorLog bitMonitorLog) {
		return super.findPage(page, bitMonitorLog);
	}
	
	@Transactional(readOnly = false)
	public void save(BitMonitorLog bitMonitorLog) {
		super.save(bitMonitorLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(BitMonitorLog bitMonitorLog) {
		super.delete(bitMonitorLog);
	}
	
}