package org.bsm.dao;

import org.bsm.model.Trole;

public interface TroleDao {
    int deleteByPrimaryKey(Integer roleid);

    int insert(Trole record);

    int insertSelective(Trole record);

    Trole selectByPrimaryKey(Integer roleid);

    int updateByPrimaryKeySelective(Trole record);

    int updateByPrimaryKey(Trole record);
}