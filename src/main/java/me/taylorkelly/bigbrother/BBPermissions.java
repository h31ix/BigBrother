package me.taylorkelly.bigbrother;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;

public class BBPermissions {

    private enum PermissionHandler 
    {
        PERMISSIONS,
        GROUP_MANAGER, 
        NONE
    }

    private static com.nijiko.permissions.PermissionHandler permissionHandler;
    private static Plugin permissionsPlugin;
    private static PermissionHandler handler;

    public static void initialize(Server server) {
        //Plugin groupManager = server.getPluginManager().getPlugin("GroupManager");
        
        /* Use FakePermissions 
         if (groupManager != null) {
            permissionPlugin = groupManager;
            handler = PermissionHandler.GROUP_MANAGER;
            String version = groupManager.getDescription().getVersion();
            BBLogging.info("Permissions enabled using: GroupManager v" + version);
        } else*/
        if(setupPermissions(server)) {
            handler = PermissionHandler.PERMISSIONS;
        } else {
            handler = PermissionHandler.NONE;
            BBLogging.severe("A permission plugin isn't loaded, only OPs can use commands");
        }
    }
    
    private static boolean setupPermissions(Server server) {
        permissionsPlugin = server.getPluginManager().getPlugin("Permissions");

        if (permissionHandler == null) {
            if (permissionsPlugin != null) {
                permissionHandler = ((Permissions) permissionsPlugin).getHandler();
                String version = permissionsPlugin.getDescription().getVersion();
                BBLogging.info("Permissions enabled using: Permissions v" + version);
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
                return ((Permissions)permissionsPlugin).getHandler().permission(player, string);
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
