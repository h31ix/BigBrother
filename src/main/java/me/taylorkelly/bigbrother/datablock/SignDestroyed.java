package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Restore sign blocks correctly.
 * 
 * @author Rob
 * 
 */
public class SignDestroyed extends BBAction {
    
    public SignDestroyed(final String player, final int type, final byte data, final Sign sign, final String world) {
        super(player, world, sign.getX(), sign.getY(), sign.getZ(), type, Byte.toString(data) + "\u0060" + getText(sign));
    }
    
    public SignDestroyed(final String player, final String[] lines, final Block block) {
        super(player, block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), 323, 0 + "\u0060" + getText(lines));
    }
    
    private static String getText(final Sign sign) {
        final String[] lines = sign.getLines();
        return getText(lines);
    }
    
    private static String getText(final String[] lines) {
        final StringBuilder message = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            message.append(lines[i]);
            if (i < (lines.length - 1)) {
                message.append("\u0060");
            }
        }
        return message.toString();
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new SignDestroyed(pi, world, x, y, z, type, data);
    }
    
    private SignDestroyed(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public SignDestroyed() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void rollback(final World wld) {
        final World currWorld = wld;//server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        
        //Format is DATABYTE\u0060Line 1\u0060Line 2\u0060Line 3\u0060Line 4\u0060Line 5 
        final String[] lines = data.split("\u0060");
        final Block block = currWorld.getBlockAt(x, y, z);
        block.setTypeId(type);
        try {
            block.setData(Byte.valueOf(lines[0]));
        } catch (final NumberFormatException e) {
            BBLogging.severe("Encountered invalid SignDestroyed block.  Note that this sign may be unrecoverable.  The bug that produces these blocks has been fixed.");
        }
        if (block.getState() instanceof Sign) {
            final Sign sign = (Sign) block.getState();
            for (int i = 1; i < (lines.length + 1); i++) {
                sign.setLine(i - 1, lines[i]);
            }
        } else {
            BBLogging.warning("Error when restoring sign: Block is currently a " + block.getState().getClass().getName() + "!");
        }
    }
    
    @Override
    public void redo(final Server server) {
        final World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        
        currWorld.getBlockAt(x, y, z).setTypeIdAndData(0, (byte) 0, true);
    }
    
    @Override
    public String toString() {
        String out = "changed a sign to read:";
        final String[] lines = data.split("\u0060");
        for (int i = 1; i < lines.length; i++) {
            out += "\n" + lines[i];
        }
        return out;
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
        return "A sign being destroyed";
    }
}
