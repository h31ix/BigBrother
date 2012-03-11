package me.taylorkelly.bigbrother.datasource;

/*
 * 
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <b>Purpose:</b>Realizes a connection pool for all JDBC connections.<br>
 * <b>Description:</b>http://java.sun.com/developer/onlineTraining/Programming/JDCBook/ conpool.html<br>
 * <b>Copyright:</b>Licensed under the Apache License, Version 2.0. http://www.apache.org/licenses/LICENSE-2.0<br>
 * <b>Company:</b>SIMPL<br>
 * 
 * @author schneimi
 * @version $Id$<br>
 * @link http://code.google.com/p/simpl09/
 */
public class ConnectionService {
    private final Vector<JDCConnection> connections;
    private final String url, user, password;
    final private long timeout = 60000;
    private final ConnectionReaper reaper;
    final private int poolsize = 10;
    
    public ConnectionService(final String url, final String user, final String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        connections = new Vector<JDCConnection>(poolsize);
        reaper = new ConnectionReaper(this);
        reaper.start();
    }
    
    public synchronized void reapConnections() {
        final long stale = System.currentTimeMillis() - timeout;
        final Enumeration<JDCConnection> connlist = connections.elements();
        
        while ((connlist != null) && (connlist.hasMoreElements())) {
            final JDCConnection conn = connlist.nextElement();
            
            if ((conn.inUse()) && (stale > conn.getLastUse()) && (!conn.validate())) {
                removeConnection(conn);
            }
        }
    }
    
    public synchronized void closeConnections() {
        final Enumeration<JDCConnection> connlist = connections.elements();
        
        while ((connlist != null) && (connlist.hasMoreElements())) {
            final JDCConnection conn = connlist.nextElement();
            removeConnection(conn);
        }
    }
    
    private synchronized void removeConnection(final JDCConnection conn) {
        connections.removeElement(conn);
    }
    
    public synchronized Connection getConnection() throws SQLException {
        JDCConnection c;
        
        for (int i = 0; i < connections.size(); i++) {
            c = connections.elementAt(i);
            if (c.lease())
                return c;
        }
        
        final Connection conn = DriverManager.getConnection(url, user, password);
        c = new JDCConnection(conn, this);
        c.lease();
        connections.addElement(c);
        
        return c.getConnection();
    }
    
    public synchronized void returnConnection(final JDCConnection conn) {
        conn.expireLease();
    }
}

class ConnectionReaper extends Thread {
    private final ConnectionService pool;
    private final long delay = 300000;
    
    ConnectionReaper(final ConnectionService pool) {
        this.pool = pool;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(delay);
            } catch (final InterruptedException e) {
            }
            pool.reapConnections();
        }
    }
}
