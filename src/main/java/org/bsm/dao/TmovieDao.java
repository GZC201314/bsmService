package org.bsm.dao;

import org.bsm.model.Tmovie;
import org.bsm.model.TmovieWithBLOBs;

public interface TmovieDao {
    int deleteByPrimaryKey(String serialNumber);

    int insert(TmovieWithBLOBs record);

    int insertSelective(TmovieWithBLOBs record);

    TmovieWithBLOBs selectByPrimaryKey(String serialNumber);

    int updateByPrimaryKeySelective(TmovieWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TmovieWithBLOBs record);

    int updateByPrimaryKey(Tmovie record);
}