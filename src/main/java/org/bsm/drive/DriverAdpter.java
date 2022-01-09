package org.bsm.drive;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;


public class DriverAdpter implements Driver {
    private final Driver driver;
    private final String version;
    private final String driveType;

    public DriverAdpter(Driver driver, String version, String driveType) {
        this.driver = driver;
        this.version = version;
        this.driveType = driveType;
    }

    public Driver getDriver() {
        return driver;
    }

    public String getVersion() {
        return version;
    }

    public String getDriveType() {
        return driveType;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return this.driver.connect(url, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return this.driver.acceptsURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return this.driver.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    /*TODO */
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

}
