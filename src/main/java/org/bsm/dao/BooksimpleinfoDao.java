package org.bsm.dao;

import org.bsm.model.Booksimpleinfo;

public interface BooksimpleinfoDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Booksimpleinfo record);

    int insertSelective(Booksimpleinfo record);

    Booksimpleinfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Booksimpleinfo record);

    int updateByPrimaryKeyWithBLOBs(Booksimpleinfo record);

    int updateByPrimaryKey(Booksimpleinfo record);
}