package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockBurn extends BBAction {
    
    private ArrayList<BBAction> bystanders;
    
    public BlockBurn() {
        super();
    }
    
    public BlockBurn(String player, Block block, String world) {
        super(player, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        torchCheck(player, block);
        surroundingSignChecks(player, block);
        signCheck(player, block);
        checkGnomesLivingOnTop(player, block);
    }
    
    public BlockBurn(Block block, String world) {
        super(ENVIRONMENT, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        torchCheck(ENVIRONMENT, block);
        surroundingSignChecks(ENVIRONMENT, block);
        signCheck(ENVIRONMENT, block);
        checkGnomesLivingOnTop(ENVIRONMENT, block);
    }
    
    @Override
    public void send() {
        for (BBAction block : bystanders) {
            block.send();
        }
        super.send();
    }
    
    @Override
    public void rollback(World wld) {
        if ((type != 51) || BBSettings.restoreFire) {
            World currWorld = wld;// server.getWorld(world);
            if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
                currWorld.loadChunk(x >> 4, z >> 4);
            }
            byte blockData = 0;
            try {
                blockData = Byte.valueOf(data);
            } catch (NumberFormatException e) {
                BBLogging.debug("Erroneous BlockBurn data field:  Data value is unparsable.  Your ActionID datatable table may have become scrambled and may not match up with legacy actionIDs.  Defaulting to 0.");
            }
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
        return new BlockBurn(pi, world, x, y, z, type, data);
    }
    
    private BlockBurn(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * @param findOwner
     * @param block
     * @param world
     */
    public BlockBurn(BBPlayerInfo player, Block block, World world) {
        super(player.getName(), world.getName(), block.getX(), block.getY(), block.getZ(), block.getTypeId(), "");
        bystanders = new ArrayList<BBAction>();
        torchCheck(player.getName(), block);
        surroundingSignChecks(player.getName(), block);
        signCheck(player.getName(), block);
        checkGnomesLivingOnTop(player.getName(), block);
    }
    
    @Override
    public String toString() {
        return "burnt up a block";
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
        return "A block destroyed by fire.";
    }
}
