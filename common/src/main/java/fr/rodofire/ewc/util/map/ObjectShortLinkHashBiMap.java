package fr.rodofire.ewc.util.map;

import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ObjectShortLinkHashBiMap<T> {
    private List<T> object = new ArrayList<>();
    private Object2ShortOpenHashMap<T> map = new Object2ShortOpenHashMap<>();

    public ObjectShortLinkHashBiMap() {
    }

    public ObjectShortLinkHashBiMap(int capacity) {
        object = new ArrayList<>(capacity);
        map = new Object2ShortOpenHashMap<>(capacity);
    }

    public ObjectShortLinkHashBiMap(List<T> objects) {
        object = new ArrayList<>(objects.size());

        short size = 0;
        for (T t : objects) {
            object.add(t);
            map.put(t, size++);
        }
    }

    public T get(short index) {
        return object.get(index);
    }

    public short get(T object) {
        return map.getShort(object);
    }

    public boolean contains(T object) {
        return map.containsKey(object);
    }

    public short size() {
        return (short) object.size();
    }

    public short put(T object) {
        if (this.map.containsKey(object))
            return this.map.getShort(object);

        short size = size();
        this.map.put(object, size);
        this.object.add(object);
        return size;
    }

    public void putAll(List<T> objects) {
        this.object.addAll(objects);
        for (T t : objects) {
            if (map.containsKey(t)) continue;
            map.put(t, size());
        }
    }

    public T remove(short index) {
        T removed = object.remove(index);
        map.removeShort(removed);
        return removed;
    }

    public T remove(T object) {
        short index = map.removeShort(object);
        return this.object.remove(index);
    }
}
