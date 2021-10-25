package org.bsm.dao;

import org.bsm.model.Tmenu;

public interface TmenuDao {
    int deleteByPrimaryKey(String id);

    int insert(Tmenu record);

    int insertSelective(Tmenu record);

    Tmenu selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Tmenu record);

    int updateByPrimaryKey(Tmenu record);
}