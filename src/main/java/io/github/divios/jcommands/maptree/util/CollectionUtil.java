package io.github.divios.jcommands.maptree.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class CollectionUtil {

    public static <E> List<E> concat(List<E> collection, E toAdd) {
        collection.add(toAdd);
        return collection;
    }

    public static <E> List<E> concatAll(List<E> collection, Collection<E> toAdd) {
        collection.addAll(toAdd);
        return collection;
    }

    public static <E> List<E> remove(List<E> collection, E toAdd) {
        List<E> clone = new ArrayList<>(collection);
        clone.remove(toAdd);
        return clone;
    }

    public static <E> List<E> removeAll(List<E> collection, Collection<E> toAdd) {
        List<E> clone = new ArrayList<>(collection);
        clone.removeAll(toAdd);
        return clone;
    }

}
