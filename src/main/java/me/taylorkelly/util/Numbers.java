/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.taylorkelly.util;

public class Numbers {
    
    public static boolean isInteger(final String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }
    
    public static boolean isNumber(final String string) {
        try {
            Double.parseDouble(string);
        } catch (final Throwable e) {
            return false;
        }
        return true;
    }
}
