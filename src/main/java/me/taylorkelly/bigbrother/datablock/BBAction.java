package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

public abstract class BBAction extends Action {
    
    
    /**
     * @param player
     * @param action
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type
     * @param data
     */
    public BBAction(BBPlayerInfo player, String world, int x, int y, int z,
            int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    public BBAction(String player, String world, int x, int y, int z, int type,
            String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public BBAction() {}
    
    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return null;
    }
}
