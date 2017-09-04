package com.thinkgem.jeesite.modules.mongodb.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.thinkgem.jeesite.modules.mongodb.model.LtcTradeData;


/**
 * 继承自MongoRepository接口，MongoRepository接口包含了常用的CRUD操作，
 * 例如：save、insert、findall等等。我们可以定义自己的查找接口，
 * @author hzf
 *
 */
public interface LtcTradeDataInterface extends MongoRepository<LtcTradeData, Long>{

	@Query(value="{'time':?0}")
	LtcTradeData findByTime(String name);
	
	@Query(value="{'time' : {'$gt' : ?0}}")
	List<LtcTradeData> findByTimePageable(long time,Pageable pageable);
	
	@Query(value="{'time' : {'$gt' : ?0, '$lt' : ?1}}")
	List<LtcTradeData> findByTimeBetween(long from, long to, Sort sort);
	
}
