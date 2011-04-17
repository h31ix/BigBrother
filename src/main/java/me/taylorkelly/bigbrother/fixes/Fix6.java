package me.taylorkelly.bigbrother.fixes;

import java.io.File;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

public class Fix6 extends Fix {

	public Fix6(File dataFolder) {
		super(dataFolder);
	}
	
	protected int version = 6;

	@Override
	public void apply() {
		if (needsUpdate(version)) {
			BBLogging.info("Updating tables for 1.7.2");
			if(BBDataTable.getInstance().executeUpdate("Upgrading to BLOBs instead of VARCHAR.","ALTER TABLE `'"+BBSettings.applyPrefix("bbdata")+"'` CHANGE `data` `data` BLOB NOT NULL DEFAULT ''"), new Object[0])) {
				updateVersion(version);
			}
			//else
				//    System.exit(0);
		}
	}
}

}
