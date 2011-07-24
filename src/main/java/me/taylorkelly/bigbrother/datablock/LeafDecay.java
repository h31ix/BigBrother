package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LeafDecay extends BBAction {

    private ArrayList<BBAction> bystanders;

    public LeafDecay(String player, Block block, String world) {
        super(player, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        torchCheck(player, block);
        surroundingSignChecks(player, block);
        signCheck(player, block);
        checkGnomesLivingOnTop(player, block);
    }

    public LeafDecay(Block block, String world) {
        super(ENVIRONMENT, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        torchCheck("Environment", block);
        surroundingSignChecks("Environment", block);
        signCheck("Environment", block);
        checkGnomesLivingOnTop("Environment", block);
    }

    public static BBAction create(Block block, String world) {
        // TODO Player handling
        return new LeafDecay(block, world);
    }

    @Override
    public void send() {
        for (BBAction block : bystanders) {
            block.send();
        }
        super.send();
    }

    public void rollback(World wld) {
        if (type != 51 || BBSettings.restoreFire) {
            World currWorld = wld;//server.getWorld(world);
            if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
                currWorld.loadChunk(x >> 4, z >> 4);
            }

            byte blockData = Byte.parseByte(data);
            currWorld.getBlockAt(x, y, z).setTypeId(type);
            currWorld.getBlockAt(x, y, z).setData(blockData);
        }
    }

    public void redo(Server server) {
        World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        currWorld.getBlockAt(x, y, z).setTypeId(0);
    }

    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new LeafDecay(pi, world, x, y, z, type, data);
    }

    private LeafDecay(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
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

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getName()
     */
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getCategory()
     */
    @Override
    public ActionCategory getCategory() {
        // TODO Auto-generated method stub
        return ActionCategory.BLOCKS;
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        return "Decaying leaves";
    }
}
