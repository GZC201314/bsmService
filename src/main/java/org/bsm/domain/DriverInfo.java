package org.bsm.domain;

import java.sql.Driver;
import java.sql.DriverAction;

/**
 * @author GZC
 * @create 2022-01-05 21:54
 * @desc 驱动信息封装类
 */
public class DriverInfo {
    final Driver driver;
    DriverAction da;

    DriverInfo(Driver driver, DriverAction action) {
        this.driver = driver;
        da = action;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof DriverInfo)
                && this.driver == ((DriverInfo) other).driver;
    }

    @Override
    public int hashCode() {
        return driver.hashCode();
    }

    @Override
    public String toString() {
        return ("driver[className=" + driver + "]");
    }

    DriverAction action() {
        return da;
    }
}
