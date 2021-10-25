package org.bsm.dao;

import org.bsm.model.Person;

public interface PersonDao {
    int insert(Person record);

    int insertSelective(Person record);
}