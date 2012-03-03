/**
 * tkelly's improvement on sk89q's YAML settings thingie.
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.spout.api.util.config.Configuration;

public class BetterConfig extends Configuration {
    
    public BetterConfig(File file) {
        super(file);
    }
    
    /**
     * Casts a value to a long. May return null.
     * 
     * @param o
     * @return
     */
    private static Long castLong(Object o) {
        if (o == null)
            return null;
        else if (o instanceof Byte)
            return (long) (Byte) o;
        else if (o instanceof Integer)
            return Long.valueOf((Integer) o);
        else if (o instanceof Double)
            return (long) (double) (Double) o;
        else if (o instanceof Float)
            return (long) (float) (Float) o;
        else if (o instanceof Long)
            return (Long) o;
        else
            return null;
    }
    
    public long getLong(String path, long defaultValue) {
        if (getValue(path) == null) {
            setValue(path, defaultValue);
        }
        return castLong(getValue(path));
    }
    
    @Override
    public int getInteger(String path, int defaultValue) {
        if (getValue(path) == null) {
            setValue(path, defaultValue);
        }
        return super.getInteger(path, defaultValue);
    }
    
    @Override
    public double getDouble(String path, double defaultValue) {
        if (getValue(path) == null) {
            setValue(path, defaultValue);
        }
        return super.getDouble(path, defaultValue);
    }
    
    @Override
    public String getString(String path, String defaultValue) {
        if (getValue(path) == null) {
            setValue(path, defaultValue);
        }
        return super.getString(path, defaultValue);
    }
    
    @Override
    public boolean getBoolean(String path, boolean defaultValue) {
        if (getValue(path) == null) {
            setValue(path, defaultValue);
        }
        return super.getBoolean(path, defaultValue);
    }
    
    @SuppressWarnings("unchecked")
    public BetterNode getNode(String path) {
        if ((getValue(path) == null) || !(getValue(path) instanceof Map)) {
            BetterNode node = new BetterNode();
            setValue(path, new HashMap<String, Object>());
            return node;
        } else {
            Object raw = getValue(path);
            return new BetterNode(path, raw);
        }
        
    }
    
    /**
     * Remove a property.
     * @param path
     */
    public void removeProperty(String path) {
        super.removeNode(path);
    }
}
