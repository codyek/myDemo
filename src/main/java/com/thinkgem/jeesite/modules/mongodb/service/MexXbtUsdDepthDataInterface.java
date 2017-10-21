package com.thinkgem.jeesite.modules.mongodb.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.thinkgem.jeesite.modules.mongodb.model.MexXbtUsdDepthData;

/**
 * 继承自MongoRepository接口，MongoRepository接口包含了常用的CRUD操作，
 * 例如：save、insert、findall等等。我们可以定义自己的查找接口，
 * @author hzf
 *
 */
public interface MexXbtUsdDepthDataInterface extends MongoRepository<MexXbtUsdDepthData, Long>{

	@Query(value="{'time':?0}")
	MexXbtUsdDepthData findByTime(String name);
	
	@Query(value="{'time' : {'$gt' : ?0, '$lt' : ?1}}")
	List<MexXbtUsdDepthData> findByTimeBetween(long from, long to, Sort sort);
	
	@Query(value="{'time' : {'$gt' : ?0}}")
	List<MexXbtUsdDepthData> findByTimePageable(long time,Pageable pageable);
	
}
