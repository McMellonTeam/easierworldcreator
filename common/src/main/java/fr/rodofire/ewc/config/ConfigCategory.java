package fr.rodofire.ewc.config;


import fr.rodofire.ewc.config.objects.BooleanConfigObject;
import fr.rodofire.ewc.config.objects.EnumConfigObject;
import fr.rodofire.ewc.config.objects.IntegerConfigObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
@SuppressWarnings("unused")
public class ConfigCategory {
    final String name;
    Map<String, BooleanConfigObject> bools = new LinkedHashMap<>();
    Map<String, IntegerConfigObject> ints = new LinkedHashMap<>();
    Map<String, EnumConfigObject> enums = new LinkedHashMap<>();

    public ConfigCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addBoolean(String name, boolean defaultValue) {
        bools.put(name, new BooleanConfigObject(defaultValue, name));
    }

    public void addBoolean(BooleanConfigObject bool) {
        bools.put(bool.getName(), bool);
    }


    public void addInt(String name, int defaultValue) {
        ints.put(name, new IntegerConfigObject(defaultValue, name));
    }

    public void addInt(IntegerConfigObject integer){
        ints.put(integer.getName(),integer);
    }

    public void addInt(String name, int defaultValue, String description, int minValue, int maxValue) {
        IntegerConfigObject cfg = new IntegerConfigObject(defaultValue, name);
        cfg.setDescription(description);
        cfg.setMinValue(minValue);
        cfg.setMaxValue(maxValue);
        ints.put(name, cfg);
    }

    public void addInt(String name, int defaultValue, int minValue, int maxValue) {
        IntegerConfigObject cfg = new IntegerConfigObject(defaultValue, minValue, maxValue, name);
        ints.put(name, cfg);
    }

    public void addBoolean(String name, String description, boolean defaultValue) {
        bools.put(name, new BooleanConfigObject(defaultValue, description, name));
    }

    public void addInt(String name, String description, int defaultValue) {
        ints.put(name, new IntegerConfigObject(defaultValue, description, name));
    }

    public void addEnum(String name, String defaultValue, Set<String> strings) {
        if (!strings.contains(defaultValue)) {
            throw new IllegalStateException("Default enum value not present in possible values");
        }
        enums.put(name, new EnumConfigObject(defaultValue, name, strings));
    }

    public void addEnum(String name, String description, String defaultValue, Set<String> strings) {
        if (!strings.contains(name)) {
            throw new IllegalStateException("Default enum value not present in possible values");
        }
        enums.put(name, new EnumConfigObject(defaultValue, name, description, strings));
    }

    public void addEnum(EnumConfigObject obj){
        enums.put(obj.getName(), obj);
    }

    public Map<String, BooleanConfigObject> getBools() {
        return bools;
    }

    public Map<String, IntegerConfigObject> getInts() {
        return ints;
    }

    public Map<String, EnumConfigObject> getEnums() {
        return enums;
    }

    public void init() {

    }

    public boolean equals(ConfigCategory category) {
        if(bools.size() != category.bools.size()) return false;
        if(ints.size() != category.ints.size()) return false;
        if(enums.size() != category.enums.size()) return false;

        Iterator<BooleanConfigObject> bool = bools.values().iterator();
        Iterator<BooleanConfigObject> bool2 = category.bools.values().iterator();
        Iterator<IntegerConfigObject> it = ints.values().iterator();
        Iterator<IntegerConfigObject> it2 = category.ints.values().iterator();
        Iterator<EnumConfigObject> enu = enums.values().iterator();
        Iterator<EnumConfigObject> enu2 = category.enums.values().iterator();
        for(int i = 0; i < category.bools.size(); i++) {
            BooleanConfigObject boo = bool.next();
            BooleanConfigObject boo2 = bool2.next();
            if (!boo.equals(boo2)) return false;
        }
        for(int i = 0; i < category.ints.size(); i++) {
            IntegerConfigObject ii = it.next();
            IntegerConfigObject ii2 = it2.next();
            if (!ii.equals(ii2)) return false;
        }
        for(int i = 0; i < category.enums.size(); i++) {
            EnumConfigObject ei = enu.next();
            EnumConfigObject ei2 = enu2.next();
            if (!ei.equals(ei2)) return false;
        }
        return name.equals(category.name);
    }

}
