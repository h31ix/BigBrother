package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Flow extends BBAction {
    
    private ArrayList<BBAction> bystanders;
    
    public Flow(String player, Block block, String world) {
        super(player, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        // TODO can't lava flow break blocks?
    }
    
    public Flow(String player, String world, int x, int y, int z, int type,
            byte data) {
        super(player, world, x, y, z, type, Byte.toString(data));
        bystanders = new ArrayList<BBAction>();
    }
    
    public void send() {
        for (BBAction block : bystanders) {
            block.send();
        }
        super.send();
    }
    
    private Flow(BBPlayerInfo player, String world, int x, int y, int z,
            int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public Flow() {
        // TODO Auto-generated constructor stub
    }
    
    public void rollback(World wld) {
        World currWorld = wld;//server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        
        currWorld.getBlockAt(x, y, z).setTypeId(0);
    }
    
    public void redo(Server server) {
    }
    
    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x,
            int y, int z, int type, String data) {
        return new Flow(pi, world, x, y, z, type, data);
    }
    
    @Override
    public String toString() {
        return "flowed " + Material.getMaterial(type).name();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getName()
     */
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getCategory()
     */
    @Override
    public ActionCategory getCategory() {
        // TODO Auto-generated method stub
        return ActionCategory.BLOCKS;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        return "Fluid movement";
    }
}
