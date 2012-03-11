package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class CreateSignText extends BBAction {
    
    public CreateSignText(final String player, final Sign sign, final String world) {
        super(player, world, sign.getX(), sign.getY(), sign.getZ(), 323, getText(sign));
    }
    
    public CreateSignText(final String player, final String[] lines, final Block block) {
        super(player, block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), 323, getText(lines));
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
        return new CreateSignText(pi, world, x, y, z, type, data);
    }
    
    private CreateSignText(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public CreateSignText() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void redo(final Server server) {
        final World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        
        final String[] lines = data.split("\u0060");
        
        final Block block = currWorld.getBlockAt(x, y, z);
        if (block.getState() instanceof Sign) {
            final Sign sign = (Sign) block.getState();
            for (int i = 0; i < lines.length; i++) {
                sign.setLine(i, lines[i]);
            }
        } else {
            BBLogging.severe("Error when redoing sign: block.getState() returned a " + block.getState().getClass().getName() + " instead of a Sign!");
        }
    }
    
    @Override
    public void rollback(final World wld) {
        final World currWorld = wld;//server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        
        final Block block = currWorld.getBlockAt(x, y, z);
        if (block.getState() instanceof Sign) {
            final Sign sign = (Sign) block.getState();
            for (int i = 0; i < sign.getLines().length; i++) {
                sign.setLine(i, "");
            }
        } else {
            BBLogging.severe("Error when restoring sign: block.getState() returned a " + block.getState().getClass().getName() + " instead of a Sign!");
        }
    }
    
    @Override
    public String toString() {
        return String.format("created a sign with text: %s", data.replace("\u0060", "\n"));
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
        return "A sign created with text.";
    }
}
