package me.taylorkelly.bigbrother.tests;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.BBDB;
import me.taylorkelly.bigbrother.tablemgrs.ActionTable;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestBigBrother {
    private File testFolder;
    
    @Before
    public void setup() {
        testFolder = new File("tests");
    }
    
    public void configGeneration() {
        final File dataFolder = new File(testFolder, "configGeneration");
        dataFolder.mkdirs();
        final File settingsFile = new File(dataFolder, "BigBrother.yml");
        BBSettings.initialize(null, dataFolder);
        Assert.assertTrue("Configuration didn't generate.", settingsFile.exists());
    }
    
    public void configLoading() throws SQLException {
        final File dataFolder = new File(testFolder, "configLoading");
        dataFolder.mkdirs();
        final File settingsFile = new File(dataFolder, "BigBrother.yml");
        
        final FileConfiguration cfg = YamlConfiguration.loadConfiguration(settingsFile);
        cfg.set("database.type", "MYSQL");
        try {
            cfg.save(settingsFile);
        } catch (IOException ex) {
            Logger.getLogger(TestBigBrother.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BBSettings.initialize(null, dataFolder);
        
        // Get database running (ActionTable init requires it)
        BBDB.reconnect(); // Do NOT catch.
        
        ActionTable.getInstance().init();
        BBSettings.loadPostponed();
        Assert.assertTrue("Configuration didn't get saved.", BBDB.usingDBMS(DBMS.MYSQL));
    }
}
