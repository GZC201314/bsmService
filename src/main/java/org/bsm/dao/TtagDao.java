package org.bsm.dao;

import org.bsm.model.Ttag;

public interface TtagDao {
    int deleteByPrimaryKey(String id);

    int insert(Ttag record);

    int insertSelective(Ttag record);

    Ttag selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Ttag record);

    int updateByPrimaryKey(Ttag record);
}