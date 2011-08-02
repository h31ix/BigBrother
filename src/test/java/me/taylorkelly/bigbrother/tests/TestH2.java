/**
 * 
 */
package me.taylorkelly.bigbrother.tests;

import java.io.File;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.BBDB;
import me.taylorkelly.bigbrother.griefcraft.util.Updater;
import me.taylorkelly.bigbrother.tablemgrs.ActionTable;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;
import me.taylorkelly.bigbrother.tablemgrs.BBWorldsTable;
import me.taylorkelly.bigbrother.tablemgrs.OwnersTable;

import org.junit.Before;
import org.junit.Test;

/**
 * Run tests against H2
 * 
 * @author Rob
 * 
 */
public class TestH2 {
    
    private static final String BBDATA_TABLE_NAME = "bbdata";
    private static final String BBUSERS_TABLE_NAME = "bbusers";
    private static final String BBWORLDS_TABLE_NAME = "bbworlds";
    private static final String OWNERS_TABLE_NAME = "owners";
    private static final String ACTION_TABLE_NAME = "actions";
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        BBSettings.initialize(null, new File("."));
        BBDB.setDBMS(DBMS.H2.name()); // Just to be sure.
        Updater updater = new Updater();
        updater.check();
        updater.update();
    }
    
    /**
     * Test method for {@link me.taylorkelly.bigbrother.datasource.BBDB#reconnect()} .
     * 
     * @throws SQLException
     */
    @Test
    public void testGetConnection() throws SQLException {
        BBDB.reconnect();
    }
    
    /**
     * Test method for creating data tables.
     */
    @Test
    public void testDataTableCreation() {
        //DROP first.
        BBDB.executeUpdate("DROP TABLE IF EXISTS " + BBDATA_TABLE_NAME);
        
        // getInstance performs init, getCreateSyntax is a NOP in this instance.
        BBDataTable.getInstance().getCreateSyntax();
        
        // Clean up singleton for next test.
        BBDataTable.cleanup();
    }
    
    /**
     * Test method for creating data tables.
     */
    @Test
    public void testActionTableCreation() {
        //DROP first.
        BBDB.executeUpdate("DROP TABLE IF EXISTS " + ACTION_TABLE_NAME);
        
        // getInstance performs init, getCreateSyntax is a NOP in this instance.
        ActionTable.getInstance().getCreateSyntax();
        
        // Clean up singleton for next test.
        ActionTable.cleanup();
    }
    
    /**
     * Test method for creating data tables.
     */
    @Test
    public void testUserTableCreation() {
        //DROP first.
        BBDB.executeUpdate("DROP TABLE IF EXISTS " + BBUSERS_TABLE_NAME);
        
        // getInstance performs init, getCreateSyntax is a NOP in this instance.
        BBUsersTable.getInstance().getCreateSyntax();
        
        // Clean up singleton for next test.
        BBUsersTable.cleanup();
    }
    
    /**
     * Test method for creating data tables.
     */
    @Test
    public void testWorldTableCreation() {
        //DROP first.
        BBDB.executeUpdate("DROP TABLE IF EXISTS " + BBWORLDS_TABLE_NAME);
        
        // getInstance performs init, getCreateSyntax is a NOP in this instance.
        BBWorldsTable.getInstance().getCreateSyntax();
        
        // Clean up singleton for next test.
        BBWorldsTable.cleanup();
    }
    
    /**
     * Test method for creating data tables.
     */
    @Test
    public void testOwnersTableCreation() {
        
        //DROP first.
        BBDB.executeUpdate("DROP TABLE IF EXISTS " + OWNERS_TABLE_NAME);
        
        // getInstance performs init, getCreateSyntax is a NOP in this instance.
        OwnersTable.getInstance().getCreateSyntax();
        
        // Clean up singleton for next test.
        OwnersTable.cleanup();
    }
    
}
