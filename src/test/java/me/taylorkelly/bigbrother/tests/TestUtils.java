package me.taylorkelly.bigbrother.tests;

import java.io.File;

import net.minecraft.server.EntityTracker;
import net.minecraft.server.SecondaryWorldServer;
import net.minecraft.server.ServerNBTManager;
import net.minecraft.server.WorldManager;
import net.minecraft.server.WorldServer;

import org.bukkit.World;
import org.bukkit.World.Environment;

public class TestUtils {
    static WorldServer world=null;
    
    /** 
     * Initialize and populate a simple world. USED FOR JUnit TESTS ONLY
     * @return a 
     */
    public static World createSimpleWorld() {
        world = new WorldServer(null, new ServerNBTManager(new File("world"), "world", true), "world", 0, 3, Environment.NORMAL, null, null); // CraftBukkit
        world.spawnMonsters = 0;
        world.setSpawnFlags(false, false);
        return world.getWorld();
    }
    
}
