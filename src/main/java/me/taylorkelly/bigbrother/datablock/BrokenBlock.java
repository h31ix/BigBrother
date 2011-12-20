package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import net.nexisonline.bigbrother.ownership.OwnershipManager;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BrokenBlock extends BBAction {
    
    public BrokenBlock(String player, Block block, String world) {
        this(player, block, world, true);
        OwnershipManager.removeOwner(block);
    }
    
    public BrokenBlock(String player, Block block, String world, boolean checks) {
        super(player, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        children = new ArrayList<BBAction>();
        if (checks) {
            torchCheck(player, block);
            surroundingSignChecks(player, block);
            signCheck(player, block);
            chestCheck(player, block);
            checkGnomesLivingOnTop(player, block);
            bedCheck(player, block);
        }
        OwnershipManager.removeOwner(block);
    }
    
    public BrokenBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, byte data) {
        super(pi, world, x, y, z, type, Byte.toString(data));
        children = new ArrayList<BBAction>();
    }
    
    @Override
    public void send() {
        for (BBAction block : children) {
            block.send();
        }
        super.send();
    }
    
    @Override
    public void rollback(World wld) {
        if ((type != 51) || BBSettings.restoreFire) {
            World currWorld = wld;//server.getWorld(world);
            if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
                currWorld.loadChunk(x >> 4, z >> 4);
            }
            
            byte blockData = Byte.parseByte(data);
            currWorld.getBlockAt(x, y, z).setTypeId(type);
            currWorld.getBlockAt(x, y, z).setData(blockData);
        }
    }
    
    @Override
    public void redo(Server server) {
        World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        
        currWorld.getBlockAt(x, y, z).setTypeId(0);
    }
    
    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new BrokenBlock(pi, world, x, y, z, type, data);
    }
    
    private BrokenBlock(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public BrokenBlock() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String toString() {
        return "broke block " + Material.getMaterial(type);
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
        return "A block destroyed by a player.";
    }
}
