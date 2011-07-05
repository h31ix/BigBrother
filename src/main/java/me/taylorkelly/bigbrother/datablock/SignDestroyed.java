package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Restore sign blocks correctly.
 * @author Rob
 *
 */
public class SignDestroyed extends BBAction {

    public SignDestroyed(String player, int type, byte data, Sign sign, String world) {
        super(player, world, sign.getX(), sign.getY(), sign.getZ(), type, Byte.toString(data)+"\u0060"+getText(sign));
    }

    public SignDestroyed(String player, String[] lines, Block block) {
        super(player, block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), 323, 0+"\u0060"+getText(lines));
    }

    private static String getText(Sign sign) {
        String[] lines = sign.getLines();
        return getText(lines);
    }

    private static String getText(String[] lines) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            message.append(lines[i]);
            if (i < lines.length - 1) {
                message.append("\u0060");
            }
        }
        return message.toString();
    }

    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new SignDestroyed(pi, world, x, y, z, type, data);
    }

    private SignDestroyed(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }


    /**
     * 
     */
    public SignDestroyed() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void rollback(World wld) {
        World currWorld = wld;//server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        //Format is DATABYTE\u0060Line 1\u0060Line 2\u0060Line 3\u0060Line 4\u0060Line 5 
        String[] lines = data.split("\u0060");
        Block block = currWorld.getBlockAt(x, y, z);
        block.setTypeId(type);
        try {
        	block.setData(Byte.valueOf(lines[0]));
        } catch(NumberFormatException e) {
        	BBLogging.severe("Encountered invalid SignDestroyed block.  Note that this sign may be unrecoverable.  The bug that produces these blocks has been fixed.");
        }
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for (int i = 1; i < lines.length+1; i++) {
                sign.setLine(i, lines[i]);
            }
        } else {
            BBLogging.warning("Error when restoring sign: Block is currently a "+block.getState().getClass().getName()+"!");
        }
    }

    @Override
    public void redo(Server server) {
        World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        currWorld.getBlockAt(x, y, z).setTypeIdAndData(0, (byte) 0, true);
    }
    
    @Override
    public String toString() {
        String out= "changed a sign to read:";
        String[] lines = data.split("\u0060");
        for(int i = 1;i<lines.length;i++) {
            out+="\n"+lines[i];
        }
        return out;
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
}
