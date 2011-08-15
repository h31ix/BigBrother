/**
 * (c)2011 BigBrother Contributors
 *
 * (LICENSED AS BSD BY N3X15)
 */
package me.taylorkelly.bigbrother.datasource;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.BetterConfig;
import me.taylorkelly.bigbrother.PropertiesFile;
import me.taylorkelly.util.TimeParser;

/**
 * Persistent database link
 * 
 * @author Rob
 * 
 */
public class BBDB {
    /**
     * For tracing potential left-open statements.
     * 
     * @author Rob
     * 
     */
    public static class StatementInfo {
        public Object stmt;
        public StackTraceElement[] stack;
        
        public StatementInfo(Object stmt) {
            this.stmt = stmt;
            stack = Thread.currentThread().getStackTrace();
        }
        
        public void close() throws SQLException {
            if (stmt instanceof Statement) {
                ((Statement) stmt).close();
            }
            if (stmt instanceof PreparedStatement) {
                ((PreparedStatement) stmt).close();
            }
        }
    }
    
    public static String prefix = "";
    public static DBMS dbms = DBMS.H2;
    public static String username = "";
    public static String password = "";
    public static String schema = "";
    public static String hostname = "";
    public static int port = 3306;
    public static boolean lowPriority = true;
    public static String engine = "INNODB";
    
    private static Connection conn;
    private static JDCConnectionDriver driver;
    public static Map<ResultSet, StatementInfo> statements = new HashMap<ResultSet, StatementInfo>();
    
    public interface DBFailCallback {
        void disableMe();
    }
    
    public static void initSettings(BetterConfig yml) {
        // Database type (Database Management System = DBMS :V)
        final String dbms = yml.getString("database.type", DBMS.H2.name());
        final String cleanse_age = yml.getString("database.cleanser.age", "7d");
        setDBMS(dbms);
        
        BBSettings.deletesPerCleansing = yml.getLong("database.cleanser.deletes-per-operation", BBSettings.deletesPerCleansing); // "The maximum number of records to delete per cleansing (0 to disable).");
        
        if (cleanse_age.equals("0s") || cleanse_age.equalsIgnoreCase("off"))
            BBSettings.cleanseAge = -1;
        else
            BBSettings.cleanseAge = TimeParser.parseInterval(cleanse_age);// "The maximum age of items in the database (can be mixture of #d,h,m,s) (0s to disable)"));
            
        BBSettings.sendDelay = yml.getInt("database.send-delay", BBSettings.sendDelay);// "Delay in seconds to batch send updates to database (4-5 recommended)");
        
        username = yml.getString("database.username", username);
        password = yml.getString("database.password", password);
        hostname = yml.getString("database.hostname", hostname);
        port = yml.getInt("database.port", port);
        schema = yml.getString("database.database", schema);
        prefix = yml.getString("database.prefix", prefix);
        
        // MySQL-specific crap
        engine = yml.getString("database.mysql.engine", engine);
        lowPriority = yml.getBoolean("database.mysql.low-priority-insert", lowPriority);
    }
    
    /**
     * Set up the connection
     * 
     * @param plugin
     * @param system
     *            DBMS in use
     * @param hostname
     *            Address of the server
     * @param username
     * @param password
     * @param schema
     *            Database
     */
    public static void init() {
    }
    
    public static void shutdown() {
        // Close open statements
        for (StatementInfo stmt : statements.values()) {
            try {
                stmt.close();
            } catch (SQLException e) {
            }
        }
        
        // Close connection
        try {
            conn.close();
        } catch (SQLException e) {
        }
        
        // Shutdown driver
        driver.shutdown();
        
    }
    
    public static void setDBMS(String name) {
        try {
            dbms = DBMS.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            dbms = DBMS.H2;
        }
    }
    
    public static boolean tableExists(String tableName) {
        boolean r = false;
        ResultSet rs = null;
        try {
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, tableName, null);
            return rs.next();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            r = false;
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return r;
    }
    
    public static void reconnect() throws SQLException {
        String driverClass = "";
        switch (dbms) {
            case H2:
                driverClass = "org.h2.Driver";
                username = "sa";
                password = "";
                break;
            case MYSQL:
                driverClass = "com.mysql.jdbc.Driver";
                break;
            case POSTGRES:
                driverClass = "org.postgresql.Driver";
                break;
        }
        try {
            driver = new JDCConnectionDriver(driverClass, getDSN(), username, password);
        } catch (InstantiationException e) {
            BBLogging.severe("Cannot instantiate the " + driverClass + " driver!", e);
        } catch (IllegalAccessException e) {
            BBLogging.severe("Cannot access the " + driverClass + " driver!", e);
        } catch (ClassNotFoundException e) {
            BBLogging.severe("Cannot find the " + driverClass + " driver!  Restart the server and try again.", e);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        conn = DriverManager.getConnection("jdbc:jdc:jdcpool");
        conn.setAutoCommit(false);
    }
    
    public static void cleanup(String caller, Statement stmt, ResultSet rs) {
        try {
            if (null != rs) {
                rs.close();
                if (statements.containsKey(rs)) {
                    statements.get(rs).close();
                    statements.remove(rs);
                }
            }
        } catch (SQLException e) {
            BBLogging.severe("Error closing recordset from '" + caller + "':", e);
        }
        
        try {
            if (null != stmt) {
                stmt.close();
            }
        } catch (SQLException e) {
            BBLogging.severe("Error closing statement from '" + caller + "':", e);
        }
    }
    
    /**
     * Determine the version of the database table currently installed.
     * 
     * @param dataFolder
     * @param table
     * @return
     */
    public static int getVersion(File dataFolder, String table) {
        File f = new File(dataFolder, "DATABASE_VERSION");
        PropertiesFile pf = new PropertiesFile(f);
        return pf.getInt(table, -1, "");
    }
    
    /**
     * Check DATABASE_VERSION file to see if a database upgrade is needed.
     * 
     * @param dataFolder
     * @return
     */
    public static boolean needsUpdate(File dataFolder, String table,
            int latestVersion) {
        boolean r = true;
        try {
            File f = new File(dataFolder, "DATABASE_VERSION");
            PropertiesFile pf = new PropertiesFile(f);
            r = (pf.getInt(table, latestVersion, "") < latestVersion);
            pf.setInt(table, latestVersion, "");
            pf.save();
        } catch (Exception e) {
            return true;
        }
        return r;
    }
    
    /**
     * Get the JDBC DSN for a specific database system, with included database-specific settings.
     * 
     * @return The DSN we want.
     */
    public static String getDSN() {
        switch (dbms) {
            case MYSQL:
                return String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true", hostname, port, schema);
            case POSTGRES:
                return String.format("jdbc:postgresql://%s:%d/%s", hostname, port, schema);
            case H2:
                return "jdbc:h2:plugins" + File.separator + "BigBrother" + File.separator + "bigbrother";
            default:
                return "";
        }
    }
    
    /**
     * Are we using a certain Database Management System?
     * 
     * @param system
     *            The database system to check against.
     * @return
     */
    public static boolean usingDBMS(DBMS system) {
        return dbms == system;
    }
    
    /**
     * Prefixify table names.
     * 
     * @param tablename
     * @return
     */
    public static String applyPrefix(String tablename) {
        return prefix + tablename;
    }
    
    public static boolean tryUpdate(String sql, Object... params) {
        return executeUpdate(sql, params) != Statement.EXECUTE_FAILED;
    }
    
    /**
     * @param cleanseAged
     * @return
     * @throws
     */
    public static int executeUpdate(String sql, Object... params) {
        PreparedStatement stmt = null;
        
        int r = Statement.EXECUTE_FAILED;
        try {
            stmt = conn.prepareStatement(sql);
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            r = stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            BBLogging.severe("executeUpdate failed (" + sql + "):", e);
        } finally {
            BBDB.cleanup("executeUpdate (" + sql + ")", stmt, null);
        }
        return r;
    }
    
    /**
     * @param statementSql
     * @return
     * @throws SQLException
     */
    public static PreparedStatement prepare(String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }
    
    /**
     * @throws SQLException
     * 
     */
    public static void commit() throws SQLException {
        conn.commit();
    }
    
    /**
     * @return
     * @throws SQLException
     */
    public static Statement createStatement() throws SQLException {
        return conn.createStatement();
    }
    
    /**
     * @param sql
     * @param name
     * @return
     */
    public static ResultSet executeQuery(String sql, Object... params) {
        PreparedStatement stmt = null;
        
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            if (!stmt.execute())
                return null;
            rs = stmt.getResultSet();
            statements.put(rs, new StatementInfo(stmt));
        } catch (SQLException e) {
            BBLogging.severe("executeQuery failed (" + sql + "):", e);
        } catch (Exception e) {
            if (e.getClass().getName().contains("CommunicationsException")) {
                BBLogging.severe("Communications failure, attempting to reconnect.", e);
                try {
                    reconnect();
                } catch (SQLException e1) {
                    BBLogging.severe("Failed to reconnect.", e1);
                }
            }
        } finally {
            //BBDB.cleanup(sql, stmt, null);
        }
        return rs;
    }
}
