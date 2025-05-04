package fr.rodofire.ewc.config;


import fr.rodofire.ewc.config.objects.BooleanConfigObject;
import fr.rodofire.ewc.config.objects.EnumConfigObject;
import fr.rodofire.ewc.config.objects.IntegerConfigObject;
import fr.rodofire.ewc.toml.Toml;

import java.util.Map;

public class ReadableConfig {
    private final String MOD_ID;

    public ReadableConfig(String modID) {
        this.MOD_ID = modID;
    }

    public void refresh(ConfigCategory caterory) {
        Toml toml = new Toml();
        toml.read(ConfigUtil.getConfigPath(MOD_ID).resolve(caterory.getName() + ".toml").toFile());
        for (Map.Entry<String, BooleanConfigObject> obj : caterory.bools.entrySet()) {
            obj.getValue().setPreviousValue(toml.getBoolean(obj.getKey()));
            obj.getValue().setActualValue(toml.getBoolean(obj.getKey()));
        }

        for (Map.Entry<String, IntegerConfigObject> obj : caterory.ints.entrySet()) {
            obj.getValue().setPreviousValue(toml.getLong(obj.getKey()).intValue());
            obj.getValue().setActualValue(toml.getLong(obj.getKey()).intValue());
        }

        for (Map.Entry<String, EnumConfigObject> obj : caterory.enums.entrySet()) {
            obj.getValue().setPreviousValue(toml.getString(obj.getKey()));
            obj.getValue().setActualValue(toml.getString(obj.getKey()));
        }
    }
}
