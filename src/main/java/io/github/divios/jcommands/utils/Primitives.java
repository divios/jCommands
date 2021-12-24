package io.github.divios.jcommands.utils;

import com.google.common.base.Preconditions;
import io.github.divios.jcommands.exceptions.primitiveFormatException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class Primitives {

    public static boolean isShort(String s) {
        return testCast(() -> Short.parseShort(s));
    }

    public static short getAsShort(String s) {
        if (!isShort(s)) throw new primitiveFormatException("String passed is not a short");
        return Short.parseShort(s);
    }

    public static boolean isInteger(String s) {
        return testCast(() -> Integer.parseInt(s));
    }

    public static int getAsInteger(String s) {
        if (!isInteger(s)) throw new primitiveFormatException("String passed is not an integer");
        return Integer.parseInt(s);
    }

    public static boolean isFloat(String s) {
        return testCast(() -> Float.parseFloat(s));
    }

    public static float getAsFloat(String s) {
        if (!isFloat(s)) throw new primitiveFormatException("String passed is not a float");
        return Float.parseFloat(s);
    }

    public static boolean isDouble(String s) {
        return testCast(() -> Double.parseDouble(s));
    }

    public static double getAsDouble(String s) {
        if (!isDouble(s)) throw new primitiveFormatException("String passed is not a double");
        return Double.parseDouble(s);
    }

    public static boolean isLong(String s) {
        return testCast(() -> Long.parseLong(s));
    }

    public static long getAsLong(String s) {
        if (!isLong(s)) throw new primitiveFormatException("String passed is not a Long");
        return Long.parseLong(s);
    }

    public static boolean isChar(String s) {
        return s.length() == 1;
    }

    public static char getAsChar(String s) {
        if (!isChar(s)) throw new primitiveFormatException("String passed is not a char");
        return s.charAt(0);
    }

    public static boolean isString(Object o) {
        return Utils.testThrow(() -> String.valueOf(o));
    }

    public static String getAsString(Object o) {
        if (!isString(o)) throw new primitiveFormatException("Object passed is not a string");
        return String.valueOf(o);
    }

    public static boolean isBoolean(String s) {
        return s.equals("true") || s.equals("false");

    }

    public static boolean getAsBoolean(String s) {
        if (!isBoolean(s)) throw new primitiveFormatException("String passed is not a boolean");
        return Boolean.parseBoolean(s);
    }

    public static boolean isPlayer(String s) {
        return Bukkit.getPlayer(s) != null;
    }

    public static Player getAsPlayer(String s) {
        Preconditions.checkArgument(isPlayer(s), "Invalid Player");
        return Bukkit.getPlayer(s);
    }

    private static boolean testCast(Runnable runnable) {
        return Utils.testThrow(runnable);
    }

}
