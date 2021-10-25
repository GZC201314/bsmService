package org.bsm.dao;

import org.bsm.model.Doubanbook;
import org.springframework.stereotype.Repository;

@Repository
public interface DoubanbookDao {
    int deleteByPrimaryKey(String isbn);

    int insert(Doubanbook record);

    int insertSelective(Doubanbook record);

    Doubanbook selectByPrimaryKey(String isbn);

    int updateByPrimaryKeySelective(Doubanbook record);

    int updateByPrimaryKeyWithBLOBs(Doubanbook record);

    int updateByPrimaryKey(Doubanbook record);
}