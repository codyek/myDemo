package com.thinkgem.jeesite.modules.platform.service.apiauthor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.platform.entity.apiauthor.APIAuthorize;
import com.thinkgem.jeesite.modules.platform.dao.apiauthor.APIAuthorizeDao;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * API授权Service
 * @author hzf
 * @version 2017-07-08
 */
@Service
@Transactional(readOnly = true)
public class APIAuthorizeService extends CrudService<APIAuthorizeDao, APIAuthorize> {

	public APIAuthorize get(String id) {
		return super.get(id);
	}
	
	/**
	 * 
	* @Title: getByUserId
	* @Description: 根据 userid 和 平台code 获取授权信息
	* @param @param uid
	* @param @param platform
	* @param @return
	* @return APIAuthorize
	* @throws
	 */
	public APIAuthorize getByUserId(APIAuthorize aPIAuthorize) {
		return dao.getByUserId(aPIAuthorize);
	}
	
	public List<APIAuthorize> findList(APIAuthorize aPIAuthorize) {
		return super.findList(aPIAuthorize);
	}
	
	public Page<APIAuthorize> findPage(Page<APIAuthorize> page, APIAuthorize aPIAuthorize) {
		return super.findPage(page, aPIAuthorize);
	}
	
	@Transactional(readOnly = false)
	public void save(APIAuthorize aPIAuthorize) {
		// 设置当前用户id 到 userid 字段
		User user = UserUtils.getUser();
		aPIAuthorize.setUser(user);
		super.save(aPIAuthorize);
	}
	
	@Transactional(readOnly = false)
	public void delete(APIAuthorize aPIAuthorize) {
		super.delete(aPIAuthorize);
	}
	
}