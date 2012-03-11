package me.taylorkelly.util;

public class Time {
    
    public static long ago(final long cleanseAge) {
        return (System.currentTimeMillis() / 1000) - cleanseAge;
    }
    
    public static String formatDuration(final long s) {
        return String.format("%dh%02dm%02ds", s / 3600, (s % 3600) / 60, (s % 60));
    }
}
