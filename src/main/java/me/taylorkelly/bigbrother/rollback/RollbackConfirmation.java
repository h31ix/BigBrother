package me.taylorkelly.bigbrother.rollback;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class RollbackConfirmation {
    private static HashMap<String, RollbackInterpreter> confirmees = new HashMap<String, RollbackInterpreter>();
    
    public static boolean hasRI(final Player player) {
        if (confirmees.containsKey(player.getName()))
            return true;
        else
            return false;
    }
    
    public static RollbackInterpreter getRI(final Player player) {
        return confirmees.remove(player.getName());
    }
    
    public static void deleteRI(final Player player) {
        confirmees.remove(player.getName());
    }
    
    public static void setRI(final Player player, final RollbackInterpreter interpreter) {
        confirmees.put(player.getName(), interpreter);
    }
    
}
