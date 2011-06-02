package me.taylorkelly.bigbrother;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class BBPermissions {

    private enum BBPermPlugin 
    {
        PERMISSIONS,
        GROUP_MANAGER, 
        NONE
    }
    public static PermissionHandler permissionHandler;
    private static BBPermPlugin handler;
    private static Plugin permissionPlugin;

    public static void initialize(Server server) {
        if(setupPermissions(server)) {
            handler = BBPermPlugin.PERMISSIONS;
            String version = permissionPlugin.getDescription().getVersion();
            
            if (!server.getPluginManager().isPluginEnabled(permissionPlugin)) {
                BBLogging.info("Permissions plugin found but disabled. Enabling 'Permissions' (v"+version+").");
                server.getPluginManager().enablePlugin(permissionPlugin);
            }
            
            BBLogging.info("Permissions enabled using: Permissions v" + version);
        } else {
            handler = BBPermPlugin.NONE;
            BBLogging.severe("A permission plugin isn't loaded, only OPs can use commands");
        }
    }

    /**
     * @param server
     * @return
     */
    private static boolean setupPermissions(Server server) { 
        permissionPlugin = server.getPluginManager().getPlugin("Permissions");

        if (permissionHandler == null) {
            if (permissionPlugin != null) {
                permissionHandler = ((Permissions) permissionPlugin).getHandler();
                return true;
            } else {
                BBLogging.warning("Permission system not detected, defaulting to OP");
            }
        }
        return false;
    }

    private static boolean permission(Player player, String string) {
        switch (handler) {
            case PERMISSIONS:
                return ((Permissions)permissionPlugin).getHandler().has(player, string);
            //case GROUP_MANAGER:
            //    return ((GroupManager)permissionPlugin).getWorldsHolder().getWorldPermissions(player).has(player, string);
            case NONE:
                return player.isOp();
            default:
                return player.isOp();
        }
    }

    public static boolean info(Player player) {
        return permission(player, "bb.admin.info");
    }

    public static boolean rollback(Player player) {
        return permission(player, "bb.admin.rollback");
    }

    public static boolean watch(Player player) {
        return permission(player, "bb.admin.watch");
    }

    public static boolean cleanse(Player player) {
        return permission(player, "bb.admin.cleanse");
    }
}
