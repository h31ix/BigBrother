/**
 * Block Listener for /bb stick
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

public enum Permissions {
    INFO("bb.admin.info"),
    ROLLBACK("bb.admin.rollback"),
    WATCH("bb.admin.watch"),
    CLEANSE("bb.admin.cleanse");
    
    public final String id;
    
    Permissions(final String permID) {
        id = permID;
    }
}
