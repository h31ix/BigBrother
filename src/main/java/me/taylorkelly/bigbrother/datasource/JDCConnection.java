package me.taylorkelly.bigbrother.datasource;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * <b>Purpose:</b>Wrapper for JDBCConnection.<br>
 * <b>Description:</b>http://java.sun.com/developer/onlineTraining/Programming/JDCBook/ conpool.html<br>
 * <b>Copyright:</b>Licensed under the Apache License, Version 2.0. http://www.apache.org/licenses/LICENSE-2.0<br>
 * <b>Company:</b>SIMPL<br>
 * 
 * @author schneimi
 * @version $Id$<br>
 * @link http://code.google.com/p/simpl09/
 */
public class JDCConnection implements Connection {
    private final ConnectionService pool;
    private final Connection conn;
    private boolean inuse;
    private long timestamp;
    
    public JDCConnection(final Connection conn, final ConnectionService pool) {
        this.conn = conn;
        this.pool = pool;
        inuse = false;
        timestamp = 0;
    }
    
    public synchronized boolean lease() {
        if (inuse)
            return false;
        else {
            inuse = true;
            timestamp = System.currentTimeMillis();
            return true;
        }
    }
    
    public boolean validate() {
        try {
            conn.getMetaData();
        } catch (final Exception e) {
            return false;
        }
        return true;
    }
    
    public boolean inUse() {
        return inuse;
    }
    
    public long getLastUse() {
        return timestamp;
    }
    
    public void close() throws SQLException {
        pool.returnConnection(this);
    }
    
    protected void expireLease() {
        inuse = false;
    }
    
    protected Connection getConnection() {
        return conn;
    }
    
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }
    
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return conn.prepareCall(sql);
    }
    
    public Statement createStatement() throws SQLException {
        return conn.createStatement();
    }
    
    public String nativeSQL(final String sql) throws SQLException {
        return conn.nativeSQL(sql);
    }
    
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        conn.setAutoCommit(autoCommit);
    }
    
    public boolean getAutoCommit() throws SQLException {
        return conn.getAutoCommit();
    }
    
    public void commit() throws SQLException {
        conn.commit();
    }
    
    public void rollback() throws SQLException {
        conn.rollback();
    }
    
    public boolean isClosed() throws SQLException {
        return conn.isClosed();
    }
    
    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }
    
    public void setReadOnly(final boolean readOnly) throws SQLException {
        conn.setReadOnly(readOnly);
    }
    
    public boolean isReadOnly() throws SQLException {
        return conn.isReadOnly();
    }
    
    public void setCatalog(final String catalog) throws SQLException {
        conn.setCatalog(catalog);
    }
    
    public String getCatalog() throws SQLException {
        return conn.getCatalog();
    }
    
    public void setTransactionIsolation(final int level) throws SQLException {
        conn.setTransactionIsolation(level);
    }
    
    public int getTransactionIsolation() throws SQLException {
        return conn.getTransactionIsolation();
    }
    
    public SQLWarning getWarnings() throws SQLException {
        return conn.getWarnings();
    }
    
    public void clearWarnings() throws SQLException {
        conn.clearWarnings();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[])
     */
    
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return conn.createArrayOf(typeName, elements);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#createBlob()
     */
    
    public Blob createBlob() throws SQLException {
        return conn.createBlob();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#createClob()
     */
    
    public Clob createClob() throws SQLException {
        return conn.createClob();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#createNClob()
     */
    
    public NClob createNClob() throws SQLException {
        return conn.createNClob();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#createSQLXML()
     */
    
    public SQLXML createSQLXML() throws SQLException {
        return conn.createSQLXML();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#createStatement(int, int)
     */
    
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return conn.createStatement(resultSetType, resultSetConcurrency);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#createStatement(int, int, int)
     */
    
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
     */
    
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return conn.createStruct(typeName, attributes);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#getClientInfo()
     */
    
    public Properties getClientInfo() throws SQLException {
        return conn.getClientInfo();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#getClientInfo(java.lang.String)
     */
    
    public String getClientInfo(final String name) throws SQLException {
        return conn.getClientInfo(name);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.sql.Connection#getHoldability()
     */
    
    public int getHoldability() throws SQLException {
        return conn.getHoldability();
    }
    
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        return conn.isWrapperFor(arg0);
    }
    
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        return conn.unwrap(arg0);
    }
    
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return conn.getTypeMap();
    }
    
    public boolean isValid(final int timeout) throws SQLException {
        return conn.isValid(timeout);
    }
    
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
    }
    
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return conn.prepareStatement(sql, autoGeneratedKeys);
    }
    
    public PreparedStatement prepareStatement(final String arg0, final int[] arg1) throws SQLException {
        return conn.prepareStatement(arg0, arg1);
    }
    
    public PreparedStatement prepareStatement(final String arg0, final String[] arg1) throws SQLException {
        return conn.prepareStatement(arg0, arg1);
    }
    
    public PreparedStatement prepareStatement(final String arg0, final int arg1, final int arg2) throws SQLException {
        return conn.prepareStatement(arg0, arg1, arg2);
    }
    
    public PreparedStatement prepareStatement(final String arg0, final int arg1, final int arg2, final int arg3) throws SQLException {
        return conn.prepareStatement(arg0, arg1, arg2, arg3);
    }
    
    public void releaseSavepoint(final Savepoint arg0) throws SQLException {
        conn.releaseSavepoint(arg0);
    }
    
    public void rollback(final Savepoint arg0) throws SQLException {
        conn.rollback(arg0);
    }
    
    public void setClientInfo(final Properties arg0) throws SQLClientInfoException {
        conn.setClientInfo(arg0);
    }
    
    public void setClientInfo(final String arg0, final String arg1) throws SQLClientInfoException {
        conn.setClientInfo(arg0, arg1);
    }
    
    public void setHoldability(final int arg0) throws SQLException {
        conn.setHoldability(arg0);
    }
    
    public Savepoint setSavepoint() throws SQLException {
        return conn.setSavepoint();
    }
    
    public Savepoint setSavepoint(final String arg0) throws SQLException {
        return conn.setSavepoint(arg0);
    }
    
    public void setTypeMap(final Map<String, Class<?>> arg0) throws SQLException {
        conn.setTypeMap(arg0);
    }

    public void setSchema(String schema) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSchema() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void abort(Executor executor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNetworkTimeout() {
        return 5000;
    }
}
