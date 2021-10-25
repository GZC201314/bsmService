package org.bsm.dao;

import org.apache.ibatis.annotations.Mapper;
import org.bsm.model.Tuser;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Mapper
@Repository
public interface TuserDao {
    /**
     * 通过名字删除用户
     * @param name 查询的用户名
     * @return 受影响的记录数
     */
    int deleteByPrimaryKey(String name);

    /**
     * 插入用户
     * @param record 待插入的用户记录
     * @return 受影响的记录数
     */
    int insert(Tuser record);

    int insertSelective(Tuser record);

    Tuser selectByPrimaryKey(String name);

    int updateByPrimaryKeySelective(Tuser record);

    int updateByPrimaryKey(Tuser record);
}