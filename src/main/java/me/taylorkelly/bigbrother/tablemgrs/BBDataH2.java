/**
 * 
 */
package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author N3X15
 *
 */
public class BBDataH2 extends BBDataTable {

    public final int revision = 1;
    public String toString() {
        return "BBData H2 Driver r"+Integer.valueOf(revision);
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.BBDataTable#onLoad()
     */
    @Override
    public void onLoad() {
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.BBDataTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
        // TODO Auto-generated method stub
        return 
        "CREATE TABLE IF NOT EXISTS `"+getTableName()+"` (" 
        + "`id` INTEGER AUTO_INCREMENT PRIMARY KEY," 
        + "`date` INT UNSIGNED NOT NULL DEFAULT '0'," 
        + "`player` INT UNSIGNED NOT NULL DEFAULT '0'," 
        + "`action` tinyint NOT NULL DEFAULT '0'," 
        + "`world` tinyint NOT NULL DEFAULT '0'," 
        + "`x` int NOT NULL DEFAULT '0'," 
        + "`y` int UNSIGNED NOT NULL DEFAULT '0'," 
        + "`z` int NOT NULL DEFAULT '0'," 
        + "`type` smallint NOT NULL DEFAULT '0'," 
        + "`data` TEXT NOT NULL DEFAULT ''," 
        + "`rbacked` boolean NOT NULL DEFAULT '0'" 
        + ");" 
        + "CREATE INDEX IF NOT EXISTS dateIndex on "+getTableName()+" (date);" 
        + "CREATE INDEX IF NOT EXISTS playerIndex on "+getTableName()+" (player);" 
        + "CREATE INDEX IF NOT EXISTS actionIndex on "+getTableName()+" (action);"
        + "CREATE INDEX IF NOT EXISTS worldIndex on "+getTableName()+" (world);" 
        + "CREATE INDEX IF NOT EXISTS posIndex on "+getTableName()+" (x,y,z);" 
        + "CREATE INDEX IF NOT EXISTS typeIndex on "+getTableName()+" (type);" 
        + "CREATE INDEX IF NOT EXISTS rbackedIndex on "+getTableName()+" (rbacked);";
    }

	@Override
	public String getCleanseAged(Long timeAgo, long deletesPerCleansing) {
		return "DELETE FROM `"+getTableName()+"` WHERE date < " + timeAgo + ";";
	}
    
	@Override
	public int getCleanseByLimit(Statement stmt, Long maxRecords, long deletesPerCleansing) throws SQLException {
		String cleansql = "DELETE FROM `"+getTableName()+"` LEFT OUTER JOIN (SELECT `id` FROM `"+getTableName()+"` ORDER BY `id` DESC LIMIT 0,"
	    	+ maxRecords
	    	+ ") AS `savedValues` ON `savedValues`.`id`=`"+getTableName()+"`.`id` WHERE `savedValues`.`id` IS NULL";
	    if (deletesPerCleansing > 0) {
	        cleansql += " LIMIT " + deletesPerCleansing;
	    }
    	return stmt.executeUpdate(cleansql);
	}
}
