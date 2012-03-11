package me.taylorkelly.bigbrother.datasource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <b>Purpose:</b>Wrapper for JDBCConnectionDriver.<br>
 * <b>Description:</b>http://java.sun.com/developer/onlineTraining/Programming/ JDCBook/ conpool.html<br>
 * <b>Copyright:</b>Licensed under the Apache License, Version 2.0. http://www.apache.org/licenses/LICENSE-2.0<br>
 * <b>Company:</b> SIMPL<br>
 * 
 * @author schneimi
 * @version $Id: JDCConnectionDriver.java 1224 2010-04-28 14:17:34Z michael.schneidt@arcor.de $<br>
 * @link http://code.google.com/p/simpl09/
 */
public class JDCConnectionDriver implements Driver {
    public static final String URL_PREFIX = "jdbc:jdc:";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;
    private final ConnectionService pool;
    
    public JDCConnectionDriver(final String driver, final String url, final String user, final String password) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        DriverManager.registerDriver(this);
        Class.forName(driver).newInstance();
        pool = new ConnectionService(url, user, password);
    }
    
    public void shutdown() {
        pool.closeConnections();
    }
    
    public Connection connect(final String url, final Properties props) throws SQLException {
        if (!url.startsWith(JDCConnectionDriver.URL_PREFIX))
            return null;
        return pool.getConnection();
    }
    
    public boolean acceptsURL(final String url) {
        return url.startsWith(JDCConnectionDriver.URL_PREFIX);
    }
    
    public int getMajorVersion() {
        return JDCConnectionDriver.MAJOR_VERSION;
    }
    
    public int getMinorVersion() {
        return JDCConnectionDriver.MINOR_VERSION;
    }
    
    public DriverPropertyInfo[] getPropertyInfo(final String str, final Properties props) {
        return new DriverPropertyInfo[0];
    }
    
    public boolean jdbcCompliant() {
        return false;
    }
}
