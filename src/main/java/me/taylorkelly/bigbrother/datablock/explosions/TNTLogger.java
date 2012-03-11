package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.HashMap;
import java.util.List;

import me.taylorkelly.bigbrother.datablock.Action;
import me.taylorkelly.bigbrother.datablock.BBAction;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class TNTLogger {
    
    public static double THRESHOLD = 10.0;
    private static HashMap<Location, String> tntMap = new HashMap<Location, String>();
    
    public static void log(final String player, final Block block) {
        tntMap.put(block.getLocation(), player);
    }
    
    public static void createTNTDataBlock(final List<Block> blockList, final Location location) {
        String player = Action.ENVIRONMENT;
        Location bestLocation = null;
        double bestDistance = THRESHOLD;
        for (final Location loc : tntMap.keySet()) {
            final double distance = distance(loc, location);
            if (distance < bestDistance) {
                bestLocation = loc;
                bestDistance = distance;
                break;
            }
        }
        if (bestLocation != null) {
            player = tntMap.remove(bestLocation);
        }
        for (final Block block : blockList) {
            final BBAction dataBlock = new TNTExplosion(player, block, location.getWorld().getName());
            dataBlock.send();
            if (block.getType() == Material.TNT) {
                TNTLogger.log(player, block);
            }
        }
    }
    
    public static double distance(final Location from, final Location to) {
        if (!from.getWorld().getName().equals(to.getWorld().getName()))
            return Double.MAX_VALUE;
        else
            return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }
}
