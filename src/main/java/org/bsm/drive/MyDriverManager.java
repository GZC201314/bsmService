package org.bsm.drive;

import lombok.extern.slf4j.Slf4j;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

import java.sql.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author GZC
 * @create 2022-01-05 21:51
 * @desc 我的驱动管理类
 */
@Slf4j
public class MyDriverManager {
    // List of registered JDBC drivers
    private final static CopyOnWriteArrayList<DriverInfo> registeredDrivers = new CopyOnWriteArrayList<>();
    private static volatile int loginTimeout = 0;
    private static volatile java.io.PrintWriter logWriter = null;
    private static volatile java.io.PrintStream logStream = null;
    private final static Object logSync = new Object();

    private MyDriverManager() {
    }


    final static SQLPermission SET_LOG_PERMISSION =
            new SQLPermission("setLog");


    final static SQLPermission DEREGISTER_DRIVER_PERMISSION =
            new SQLPermission("deregisterDriver");


    public static java.io.PrintWriter getLogWriter() {
        return logWriter;
    }


    public static void setLogWriter(java.io.PrintWriter out) {

        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(SET_LOG_PERMISSION);
        }
        logStream = null;
        logWriter = out;
    }


    @CallerSensitive
    public static Connection getConnection(String url,
                                           java.util.Properties info) throws SQLException {

        return (getConnection(url, info, Reflection.getCallerClass()));
    }

    @CallerSensitive
    public static Connection getConnection(String url,
                                           String user, String password, String driverVersion, String driverType) throws SQLException {
        java.util.Properties info = new java.util.Properties();

        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }
        if (driverVersion != null) {
            info.put("driverVersion", driverVersion);
        }
        if (driverType != null) {
            info.put("driverType", driverType);
        }

        return (getConnection(url, info, Reflection.getCallerClass(1)));
    }

    @CallerSensitive
    public static Connection getConnection(String url)
            throws SQLException {

        java.util.Properties info = new java.util.Properties();
        return (getConnection(url, info, Reflection.getCallerClass(1)));
    }

    @CallerSensitive
    public static Driver getDriver(String url)
            throws SQLException {

        log.info("DriverManager.getDriver(\"" + url + "\")");

        Class<?> callerClass = Reflection.getCallerClass(1);

        // Walk through the loaded registeredDrivers attempting to locate someone
        // who understands the given URL.
        for (DriverInfo aDriver : registeredDrivers) {
            // If the caller does not have permission to load the driver then
            // skip it.
            if (isDriverAllowed(aDriver.driver, callerClass)) {
                try {
                    if (aDriver.driver.acceptsURL(url)) {
                        log.info("getDriver returning " + aDriver.driver.getClass().getName());
                        return (aDriver.driver);
                    }

                } catch (SQLException sqe) {
                    // Drop through and try the next driver.
                }
            } else {
                log.info("    skipping: " + aDriver.driver.getClass().getName());
            }

        }

        log.error("getDriver: no suitable driver");
        throw new SQLException("No suitable driver", "08001");
    }


    public static synchronized void registerDriver(DriverAdpter driver)
            throws SQLException {

        registerDriver(driver, null);
    }

    public static synchronized void registerDriver(DriverAdpter driver,
                                                   DriverAction da)
            throws SQLException {

        /* Register the driver if it has not already been added to our list */
        if (driver != null) {
            registeredDrivers.addIfAbsent(new DriverInfo(driver, da));
        } else {
            // This is for compatibility with the original DriverManager
            throw new NullPointerException();
        }

        log.info("registerDriver: " + driver);
    }

    @CallerSensitive
    public static synchronized void deregisterDriver(DriverAdpter driver)
            throws SQLException {
        if (driver == null) {
            return;
        }

        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(DEREGISTER_DRIVER_PERMISSION);
        }

        log.info("DriverManager.deregisterDriver: " + driver);

        DriverInfo aDriver = new DriverInfo(driver, null);
        if (registeredDrivers.contains(aDriver)) {
            if (isDriverAllowed(driver, Reflection.getCallerClass(1))) {
                DriverInfo di = registeredDrivers.get(registeredDrivers.indexOf(aDriver));
                // If a DriverAction was specified, Call it to notify the
                // driver that it has been deregistered
                if (di.action() != null) {
                    di.action().deregister();
                }
                registeredDrivers.remove(aDriver);
            } else {
                // If the caller does not have permission to load the driver then
                // throw a SecurityException.
                throw new SecurityException();
            }
        } else {
            log.error("    couldn't find driver to unload");
        }
    }


    @CallerSensitive
    public static java.util.Enumeration<Driver> getDrivers() {
        java.util.Vector<Driver> result = new java.util.Vector<>();

        Class<?> callerClass = Reflection.getCallerClass(1);

        // Walk through the loaded registeredDrivers.
        for (DriverInfo aDriver : registeredDrivers) {
            // If the caller does not have permission to load the driver then
            // skip it.
            if (isDriverAllowed(aDriver.driver, callerClass)) {
                result.addElement(aDriver.driver);
            } else {
                log.info("    skipping: " + aDriver.getClass().getName());
            }
        }
        return (result.elements());
    }


    public static void setLoginTimeout(int seconds) {
        loginTimeout = seconds;
    }


    public static int getLoginTimeout() {
        return (loginTimeout);
    }


    @Deprecated
    public static void setLogStream(java.io.PrintStream out) {

        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(SET_LOG_PERMISSION);
        }

        logStream = out;
        if (out != null) {
            logWriter = new java.io.PrintWriter(out);
        } else {
            logWriter = null;
        }
    }

    @Deprecated
    public static java.io.PrintStream getLogStream() {
        return logStream;
    }


    private static boolean isDriverAllowed(Driver driver, Class<?> caller) {
        ClassLoader callerCL = caller != null ? caller.getClassLoader() : null;
        return isDriverAllowed(driver, callerCL);
    }

    private static boolean isDriverAllowed(Driver driver, ClassLoader classLoader) {
        boolean result = false;
        if (driver != null) {
            Class<?> aClass = null;
            try {
                aClass = Class.forName(driver.getClass().getName(), true, classLoader);
            } catch (Exception ex) {
                result = false;
            }

            result = aClass == driver.getClass();
        }

        return result;
    }


    //  Worker method called by the public getConnection() methods.
    private static Connection getConnection(
            String url, java.util.Properties info, Class<?> caller) throws SQLException {
        /*
         * When callerCl is null, we should check the application's
         * (which is invoking this class indirectly)
         * classloader, so that the JDBC driver class outside rt.jar
         * can be loaded from here.
         */
        ClassLoader callerCL = caller != null ? caller.getClassLoader() : null;
        synchronized (MyDriverManager.class) {
            // synchronize loading of the correct classloader.
            if (callerCL == null) {
                callerCL = Thread.currentThread().getContextClassLoader();
            }
        }

        if (url == null) {
            log.error("The url cannot be null");
            throw new SQLException("The url cannot be null", "08001");
        }

        log.info("DriverManager.getConnection(\"" + url + "\")");

        // Walk through the loaded registeredDrivers attempting to make a connection.
        // Remember the first exception that gets raised so we can reraise it.
        SQLException reason = null;

        /*寻找对应版本的驱动，如果不存在的话，才去找那些版本不一样，但是兼容的驱动*/
        for (DriverInfo aDriver : registeredDrivers) {
            // If the caller does not have permission to load the driver then
            // skip it.
            if (isDriverAllowed(aDriver.driver, callerCL)) {
                try {
                    log.info("    trying " + aDriver.driver.getClass().getName());
                    Connection con = null;
//                    在这边通过驱动类型和版本来获取想要的驱动建立连接
                    if (info.get("driverVersion").equals(aDriver.driver.getVersion()) && info.get("driverType").equals(aDriver.driver.getDriveType())) {
                        con = aDriver.driver.connect(url, info);
                    }
                    if (con != null) {
                        log.info("getConnection returning " + aDriver.driver.getClass().getName());
                        return (con);
                    }
                } catch (SQLException ex) {
                    if (reason == null) {
                        reason = ex;
                    }
                }

            } else {
                log.info("    skipping: " + aDriver.getClass().getName());
            }
        }

        /*需要兼容的驱动*/
        for (DriverInfo aDriver : registeredDrivers) {
            // If the caller does not have permission to load the driver then
            // skip it.
            if (isDriverAllowed(aDriver.driver, callerCL)) {
                try {
                    log.info("    trying " + aDriver.driver.getClass().getName());
                    Connection con = aDriver.driver.connect(url, info);
                    if (con != null) {
                        log.info("getConnection returning " + aDriver.driver.getClass().getName());
                        return (con);
                    }
                } catch (SQLException ex) {
                    if (reason == null) {
                        reason = ex;
                    }
                }

            } else {
                log.info("    skipping: " + aDriver.getClass().getName());
            }
        }

        // if we got here nobody could connect.
        if (reason != null) {
            log.error("getConnection failed: " + reason);
            throw reason;
        }

        log.error("getConnection: no suitable driver found for " + url);
        throw new SQLException("No suitable driver found for " + url, "08001");
    }


}
