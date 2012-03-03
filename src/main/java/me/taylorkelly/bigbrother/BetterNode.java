/**
 * Improved ConfigurationNode
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

import org.spout.api.util.config.ConfigurationNode;

public class BetterNode extends ConfigurationNode {
    
    protected BetterNode(String path, Object value) {
        super(path, value);
    }
    
    public BetterNode() {
        this("", null);
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
    
    public long getLong(long defaultValue) {
        if (getValue() == null) {
            setValue(defaultValue);
        }
        return castLong(getValue());
    }
    
    @Override
    public double getDouble(double defaultValue) {
        if (getValue() == null) {
            setValue(defaultValue);
        }
        return super.getDouble(defaultValue);
    }
    
    @Override
    public int getInteger(int defaultValue) {
        if (getValue() == null) {
            setValue(defaultValue);
        }
        return super.getInteger(defaultValue);
    }
    
    @Override
    public String getString(String defaultValue) {
        if (getValue() == null) {
            setValue(defaultValue);
        }
        return super.getString(defaultValue);
    }
    
    @Override
    public boolean getBoolean(boolean defaultValue) {
        if (getValue() == null) {
            setValue(defaultValue);
        }
        return super.getBoolean(defaultValue);
    }
}
