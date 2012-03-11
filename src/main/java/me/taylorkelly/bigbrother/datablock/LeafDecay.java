package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LeafDecay extends BBAction {
    
    private ArrayList<BBAction> bystanders;
    
    public LeafDecay(final String player, final Block block, final String world) {
        super(player, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        torchCheck(player, block);
        surroundingSignChecks(player, block);
        signCheck(player, block);
        checkGnomesLivingOnTop(player, block);
    }
    
    public LeafDecay(final Block block, final String world) {
        super(ENVIRONMENT, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        torchCheck("Environment", block);
        surroundingSignChecks("Environment", block);
        signCheck("Environment", block);
        checkGnomesLivingOnTop("Environment", block);
    }
    
    public static BBAction create(final Block block, final String world) {
        // TODO Player handling
        return new LeafDecay(block, world);
    }
    
    @Override
    public void send() {
        for (final BBAction block : bystanders) {
            block.send();
        }
        super.send();
    }
    
    @Override
    public void rollback(final World wld) {
        if ((type != 51) || BBSettings.restoreFire) {
            final World currWorld = wld;//server.getWorld(world);
            if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
                currWorld.loadChunk(x >> 4, z >> 4);
            }
            
            final byte blockData = Byte.parseByte(data);
            currWorld.getBlockAt(x, y, z).setTypeId(type);
            currWorld.getBlockAt(x, y, z).setData(blockData);
        }
    }
    
    @Override
    public void redo(final Server server) {
        final World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        
        currWorld.getBlockAt(x, y, z).setTypeId(0);
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new LeafDecay(pi, world, x, y, z, type, data);
    }
    
    private LeafDecay(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public LeafDecay() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String toString() {
        return "decayed leaves";
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
        return "Decaying leaves";
    }
}
