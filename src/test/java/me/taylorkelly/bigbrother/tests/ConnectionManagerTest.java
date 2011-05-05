/**
 * 
 */
package me.taylorkelly.bigbrother.tests;

import java.io.File;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.datasource.BBDB;
import me.taylorkelly.bigbrother.griefcraft.util.Updater;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Rob
 * 
 */
public class ConnectionManagerTest {
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        BBSettings.initialize(null, new File("."));
        Updater updater = new Updater();
        updater.check();
        updater.update();
    }
    
    /**
     * Test method for
     * {@link me.taylorkelly.bigbrother.datasource.ConnectionManager#getConnection()}
     * .
     * @throws SQLException 
     */
    @Test
    public void testGetConnection() throws SQLException {
        BBDB.reconnect();
    }
    
}
