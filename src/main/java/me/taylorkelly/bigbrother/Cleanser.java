/**
 * Clean up old records from the database.
 * Copyright (C) 2011 BigBrother Contributors
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.taylorkelly.bigbrother;

import java.sql.Statement;

import me.taylorkelly.bigbrother.datasource.BBDB;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.util.Time;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

// Rule 1 - If you end up using ResultSet, you're doing it wrong.
public class Cleanser {
    
    private static CleanupThread cleanupThread = null;
    private static int cleanupTask;
    
    public static boolean needsCleaning() {
        return BBSettings.cleanseAge != -1;
    }
    
    /**
     * Start the cleaner thread and pray to God it finishes.
     * 
     * @param player Person to blame for the lag. Or null.
     */
    public static void clean(Player player) {
        if ((cleanupThread != null) && cleanupThread.done) {
            cleanupThread = null;
        }
        if ((cleanupThread == null) || !cleanupThread.isAlive()) {
            cleanupThread = new CleanupThread(player);
            cleanupThread.start();
        } else {
            if (player != null) {
                player.chat(BigBrother.premessage + " The cleaner is already busy.  Try again later.");
            }
        }
    }
    
    public static void initialize(BigBrother bigbrother) {
        cleanupTask = bigbrother.getServer().getScheduler().scheduleAsyncRepeatingTask(bigbrother, new CleanupTask(), 50, 26000);
        if (cleanupTask < 0) {
            BBLogging.severe("Cannot schedule the cleanup task.");
        }
    }
    
    public static void shutdown(BigBrother bb) {
        if (cleanupTask >= 0) {
            bb.getServer().getScheduler().cancelTask(cleanupTask);
        }
    }
    
    public static class CleanupTask implements Runnable {
        public void run() {
            clean(null);
        }
    }
    
    /**
     * Cleanser thread, to avoid blocking the main app when cleaning crap.
     * 
     * @author N3X15 <nexis@7chan.org>
     * 
     */
    private static class CleanupThread extends Thread {
        
        private long cleanedSoFarAge = 0;
        private boolean done = false;
        private final Player player;
        
        /**
         * @param p The player. Can be null.
         */
        public CleanupThread(Player p) {
            // Constructor.
            player = p;
            setName("Cleanser");
        }
        
        @Override
        public void run() {
            //BBLogging.info("Starting Cleanser thread...");
            if (BBSettings.cleanseAge != -1) {
                cleanByAge();
            }
            //BBLogging.info("Ending Cleanser thread...");
            done = true; // Wait for cleanup
        }
        
        private void cleanByAge() {
            long start = System.currentTimeMillis() / 1000;
            cleanedSoFarAge = BBDB.executeUpdate(BBDataTable.getInstance().getCleanseAged(Long.valueOf(Time.ago(BBSettings.cleanseAge)), BBSettings.deletesPerCleansing));
            if (cleanedSoFarAge == Statement.EXECUTE_FAILED)
                return;
            String timespent = Time.formatDuration((System.currentTimeMillis() / 1000) - start);
            
            String words = String.format("Removed %d records older than %s in %s.", cleanedSoFarAge, Time.formatDuration(BBSettings.cleanseAge), timespent);
            if (player == null) {
                BBLogging.info(words);
            } else {
                synchronized (player) {
                    player.sendMessage(ChatColor.BLUE + words);
                }
            }
        }
    }
}
