package org.bsm.dao;

import org.bsm.model.Tauthorize;

public interface TauthorizeDao {
    int deleteByPrimaryKey(String id);

    int insert(Tauthorize record);

    int insertSelective(Tauthorize record);

    Tauthorize selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Tauthorize record);

    int updateByPrimaryKey(Tauthorize record);
}