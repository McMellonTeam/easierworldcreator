package fr.rodofire.ewc.util.map;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ObjectIntLinkHashBiMap<T> {
    private List<T> object = new ArrayList<>();
    private Object2IntMap<T> map = new Object2IntOpenHashMap<>();

    public ObjectIntLinkHashBiMap() {
    }

    public ObjectIntLinkHashBiMap(int capacity) {
        object = new ArrayList<>(capacity);
        map = new Object2IntOpenHashMap<>(capacity);
    }

    public ObjectIntLinkHashBiMap(List<T> objects) {
        object = new ArrayList<>(objects.size());

        int size = 0;
        for (T t : objects) {
            object.add(t);
            map.put(t, size++);
        }
    }

    public T get(short index) {
        return object.get(index);
    }

    public int get(T object) {
        return map.getInt(object);
    }

    public boolean contains(T object) {
        return map.containsKey(object);
    }

    public int size() {
        return object.size();
    }

    public T put(T object) {
        this.map.put(object, (short) size());
        this.object.add(object);
        return object;
    }

    public void putAll(List<T> objects) {
        this.object.addAll(objects);
        for (T t : objects) {
            map.put(t, size());
        }
    }

    public T remove(short index) {
        T removed = object.remove(index);
        map.removeInt(removed);
        return removed;
    }

    public T remove(T object) {
        int index = map.removeInt(object);
        return this.object.remove(index);
    }
}
