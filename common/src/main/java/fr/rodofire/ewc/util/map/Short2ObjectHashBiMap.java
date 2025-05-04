package fr.rodofire.ewc.util.map;

import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortSet;

import java.util.Collection;

@SuppressWarnings("unused")
public class Short2ObjectHashBiMap<T> {
    private final Short2ObjectMap<T> short2Obj = new Short2ObjectOpenHashMap<>();
    private final Object2ShortOpenHashMap<T> obj2Short = new Object2ShortOpenHashMap<>();

    public Short2ObjectHashBiMap() {
    }

    public void put(T obj, short key) {
        short2Obj.put(key, obj);
        obj2Short.put(obj, key);
    }

    public T remove(short key) {
        T obj = short2Obj.remove(key);
        obj2Short.removeShort(obj);
        return obj;
    }

    public short remove(T obj) {
        if (!obj2Short.containsKey(obj)) {
            return -1; // Valeur par défaut pour indiquer l'absence de l'objet
        }
        short key = obj2Short.removeShort(obj);
        short2Obj.remove(key);
        return key;
    }

    public boolean contains(short key) {
        return short2Obj.containsKey(key);
    }

    public boolean contains(T obj) {
        return obj2Short.containsKey(obj);
    }

    public ShortSet getShorts() {
        return short2Obj.keySet();
    }

    public Collection<T> getObjects() {
        return obj2Short.keySet();
    }

    public short get(T obj) {
        return obj2Short.getShort(obj);
    }

    public T get(short key) {
        return short2Obj.get(key);
    }

    public int size() {
        return short2Obj.size();
    }

    public void clear() {
        short2Obj.clear();
        obj2Short.clear();
    }
}
