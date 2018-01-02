package com.thinkgem.jeesite.modules.platform.service.inter;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.platform.entity.inter.Interfaces;
import com.thinkgem.jeesite.modules.platform.dao.inter.InterfacesDao;

/**
 * 各平台接口信息Service
 * @author hzf
 * @version 2017-07-06
 */
@Service
@Transactional(readOnly = true)
public class InterfacesService extends CrudService<InterfacesDao, Interfaces> {

	public Interfaces get(String id) {
		return super.get(id);
	}
	
	public List<Interfaces> findList(Interfaces interfaces) {
		return super.findList(interfaces);
	}
	
	public Page<Interfaces> findPage(Page<Interfaces> page, Interfaces interfaces) {
		return super.findPage(page, interfaces);
	}
	
	@Transactional(readOnly = false)
	public void save(Interfaces interfaces) {
		super.save(interfaces);
	}
	
	@Transactional(readOnly = false)
	public void delete(Interfaces interfaces) {
		super.delete(interfaces);
	}
	
}