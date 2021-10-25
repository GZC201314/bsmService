package org.bsm.dao;

import org.bsm.model.Address;

public interface AddressDao {
    int insert(Address record);

    int insertSelective(Address record);
}