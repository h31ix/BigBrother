package me.taylorkelly.bigbrother.rollback;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datasource.BBDB;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;
import me.taylorkelly.bigbrother.tablemgrs.BBWorldsTable;

public abstract class RollbackPreparedStatement {
	
	private static RollbackPreparedStatement instance=null;
	
	public static RollbackPreparedStatement getInstance() {
		if(instance==null) {
            if(BBDB.usingDBMS(DBMS.MYSQL))
                instance=new RollbackPreparedStatementMySQL();
            else if(BBDB.usingDBMS(DBMS.POSTGRES))
                instance=new RollbackPreparedStatementPostgreSQL();
            else
                instance=new RollbackPreparedStatementH2();
        }
        return instance;
	}

    public String create(Rollback rollback, WorldManager manager) {
        StringBuilder statement = new StringBuilder("SELECT bbdata.id, date, player, action, x, y, z, type, data, rbacked, bbworlds.name AS `world`");
        statement.append(" FROM");
        statement.append(" "+BBDataTable.getInstance().getTableName() + " AS bbdata ");
        statement.append(", "+BBWorldsTable.getInstance().getTableName()+" AS bbworlds ");
        statement.append(", "+BBUsersTable.getInstance().getTableName()+" AS usr ");
        statement.append(" WHERE ");
        statement.append(" bbworlds.id = bbdata.world AND bbdata.player = usr.id AND ");
        statement.append(getActionString(rollback));
        
        if (!rollback.rollbackAll) {
            statement.append(" AND ");
            statement.append(getPlayerString(rollback.players));
        }
        if (rollback.blockTypes.size() > 0) {
            statement.append(" AND ");
            statement.append(getBlockString(rollback.blockTypes));
        }
        if (rollback.time != 0) {
            statement.append(" AND ");
            statement.append("date > ");
            statement.append("'");
            statement.append(rollback.time);
            statement.append("'");
        }
        if (rollback.radius != 0) {
            statement.append(" AND ");
            statement.append("x < ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("x > ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y < ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y > ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z < ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z > ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() - rollback.radius);
            statement.append("'");
            statement.append(" AND bbdata.world = '");
            statement.append(manager.getWorld(rollback.center.getWorld().getName()));
            statement.append("'");
        }
        if(BBDB.usingDBMS(DBMS.H2))
        	statement.append(" AND rbacked = false");
        else
        	statement.append(" AND rbacked = '0'");
        
        statement.append(" ORDER BY bbdata.id DESC");
        statement.append(";");
        return statement.toString();
    }

    private StringBuilder getBlockString(ArrayList<Integer> blockTypes) {
        StringBuilder ret = new StringBuilder("type IN(");
        for (int i = 0; i < blockTypes.size(); i++) {
            ret.append("'");
            ret.append(blockTypes.get(i));
            ret.append("'");
            if (i + 1 < blockTypes.size()) {
                ret.append(",");
            }
        }
        ret.append(")");
        return ret;
    }

    private StringBuilder getPlayerString(ArrayList<String> players) {
        StringBuilder ret = new StringBuilder("player IN (");
        for (int i = 0; i < players.size(); i++) {
            ret.append("'");
            ret.append(BBUsersTable.getInstance().getUserByName(players.get(i)).getID());
            ret.append("'");
            if (i + 1 < players.size()) {
                ret.append(",");
            }
        }
        ret.append(")");
        return ret;
    }

    // @QA-Tested andrewkm  6/12/2011
    private StringBuilder getActionString(Rollback rollback) {
        StringBuilder ret = new StringBuilder("action IN(");
        boolean first=true;
        for(Integer act:rollback.allowedActions) {
            if(first) {
                first=false;
            } else {
                ret.append(",");
            }
            ret.append("'");
            ret.append(act);
            ret.append("'");
        }
        ret.append(")");
        return ret;
    }

    public String update(Rollback rollback, WorldManager manager) {
        StringBuilder statement = new StringBuilder("UPDATE ");
        statement.append(" "+BBDataTable.getInstance().getTableName() + " AS bbdata");
        if(BBDB.usingDBMS(DBMS.H2))
        	statement.append(" SET rbacked = true");
        else
        	statement.append(" SET rbacked = '1'");
        statement.append(" WHERE ");
        statement.append(getActionString(rollback));
        if (!rollback.rollbackAll) {
            statement.append(" AND ");
            statement.append(getPlayerString(rollback.players));
        }
        if (rollback.blockTypes.size() > 0) {
            statement.append(" AND ");
            statement.append(getBlockString(rollback.blockTypes));
        }
        if (rollback.time != 0) {
            statement.append(" AND ");
            statement.append("date > ");
            statement.append("'");
            statement.append(rollback.time);
            statement.append("'");
        }
        if (rollback.radius != 0) {
            statement.append(" AND ");
            statement.append("x < ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("x > ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y < ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y > ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z < ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z > ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() - rollback.radius);
            statement.append("'");
            statement.append(" AND world = ");
            statement.append("'");
            statement.append(rollback.server.getWorlds().indexOf(rollback.center.getWorld()));
            statement.append("'");
            statement.append(" AND world = '");
            statement.append(manager.getWorld(rollback.center.getWorld().getName()));
            statement.append("'");
        }

        if(BBDB.usingDBMS(DBMS.H2))
        	statement.append(" AND rbacked = false");
        else
        	statement.append(" AND rbacked = '0'");
        statement.append(";");
        return statement.toString();
    }

    public String undoStatement(Rollback rollback, WorldManager manager) {
        StringBuilder statement = new StringBuilder("UPDATE ");
        statement.append(" "+BBDataTable.getInstance().getTableName() + " AS bbdata");
        if(BBDB.usingDBMS(DBMS.H2))
        	statement.append(" SET rbacked = false");
        else
        	statement.append(" SET rbacked = '0'");
        statement.append(" WHERE ");
        statement.append(getActionString(rollback));
        if (!rollback.rollbackAll) {
            statement.append(" AND ");
            statement.append(getPlayerString(rollback.players));
        }
        if (rollback.blockTypes.size() > 0) {
            statement.append(" AND ");
            statement.append(getBlockString(rollback.blockTypes));
        }
        if (rollback.time != 0) {
            statement.append(" AND ");
            statement.append("date > ");
            statement.append("'");
            statement.append(rollback.time);
            statement.append("'");
        }
        if (rollback.radius != 0) {
            statement.append(" AND ");
            statement.append("x < ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("x > ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y < ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y > ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z < ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z > ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() - rollback.radius);
            statement.append("'");
            statement.append(" AND world = ");
            statement.append("'");
            statement.append(rollback.server.getWorlds().indexOf(rollback.center.getWorld()));
            statement.append("'");
            statement.append(" AND world = '");
            statement.append(manager.getWorld(rollback.center.getWorld().getName()));
            statement.append("'");
        }

        if(BBDB.usingDBMS(DBMS.H2))
        	statement.append(" AND rbacked = true");
        else
        	statement.append(" AND rbacked = '1'");
        statement.append(";");
        return statement.toString();
    }
}
