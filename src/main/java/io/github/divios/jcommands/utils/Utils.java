package io.github.divios.jcommands.utils;

import java.util.function.Supplier;

public class Utils {

    public static boolean testThrow(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static <T> T testOrDefault(Supplier<T> supplier, T defVal) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return defVal;
        }
    }

}
