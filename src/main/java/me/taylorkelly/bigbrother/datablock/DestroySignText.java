package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class DestroySignText extends BBAction {

    public DestroySignText(String name, Sign sign, String world) {
        super(name, world, sign.getX(), sign.getY(), sign.getZ(), 323, getText(sign));
    }

    private static String getText(Sign sign) {
        StringBuilder message = new StringBuilder();
        String[] lines = sign.getLines();
        for (int i = 0; i < lines.length; i++) {
            message.append(lines[i]);
            if (i < lines.length - 1) {
                message.append("\u0060");
            }
        }
        return message.toString();
    }

    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new DestroySignText(pi, world, x, y, z, type, data);
    }

    private DestroySignText(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }

    public DestroySignText(BBPlayerInfo player, Sign sign, String world) {
        super(player, world, sign.getX(), sign.getY(), sign.getZ(), 323, getText(sign));
    }

    /**
     * 
     */
    public DestroySignText() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void rollback(World wld) {
        World currWorld = wld;//server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        String[] lines = data.split("\u0060");


        Block block = currWorld.getBlockAt(x, y, z);
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for (int i = 0; i < lines.length; i++) {
                sign.setLine(i, lines[i]);
            }
        } else {
            BBLogging.warning("Error when restoring sign");
        }
    }

    @Override
    public void redo(Server server) {
        World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        Block block = currWorld.getBlockAt(x, y, z);
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for (int i = 0; i < sign.getLines().length; i++) {
                sign.setLine(i, "");
            }
        } else {
            BBLogging.warning("Error when restoring sign");
        }
    }
    
    @Override
    public String toString() {
        String[] lines = data.split("\u0060");
        String out = "destroyed a sign with text: ";
        for(String line:lines){
            out+="\n"+line;
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
        return ActionCategory.MISC;
    }
}
