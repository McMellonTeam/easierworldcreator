package fr.rodofire.ewc.config.objects;

@SuppressWarnings("unused")
public abstract class AbstractConfigObject<T> {
    /**
     * the key of the name used in the screen.
     * <p> letters should be undercase
     * <p>in your translation file, you should translate "config.[mod_id].[key]"
     * if not set, the [key] value will be set to [name]
     * <p>  the key of the description used in the screen.
     * <p> letters should be undercase
     * <p>in your translation file, you should translate "config.[mod_id].[key].description"
     */
    String key;
    /**
     * the name used in the toml file
     */
    String name;
    /**
     * the description used in the toml file
     */
    String description;
    T defaultValue;
    T actualValue;
    T previousValue;
    public boolean requireRestart = false;
    boolean restart = false;

    public AbstractConfigObject(String description, String name, T defaultValue) {
        this.description = description;
        this.defaultValue = defaultValue;
        this.actualValue = defaultValue;
        this.previousValue = defaultValue;
        this.key = name;
        this.name = name;
    }

    public AbstractConfigObject(String name, T defaultValue) {
        this.defaultValue = defaultValue;
        this.actualValue = defaultValue;
        this.previousValue = defaultValue;
        this.key = name;
        this.name = name;
    }

    public abstract String getObjectCategory();

    public void resetToDefaultValue() {
        actualValue = defaultValue;
    }

    public T getActualValue() {
        return actualValue;
    }

    public void setActualValue(T actualValue) {
        if (requireRestart) {
            restart = !actualValue.equals(this.previousValue);
        }
        this.actualValue = actualValue;
    }


    /**
     * method to known if the game should restart after the modification of the value stored in the config
     * @return true if yes, false else
     */
    public boolean shouldRestart() {
        return restart;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getDefaultDescription(String modId) {
        return description == null ? ("# Possible Values:\n") : "#" + description + ("\n# Possible Values:\n");
    }

    public T getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(T previousValue) {
        this.previousValue = previousValue;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionKey(String modId) {
        return "config." + modId + "." + key + ".description";
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean equals(AbstractConfigObject<T> other) {
        if (name != null && other.name != null)
            if (!name.equals(other.name)) return false;

        if (!(other.name == null && name == null))
            return false;

        if (actualValue != null && other.actualValue != null)
            if (!actualValue.equals(other.actualValue)) return false;

        if (!(other.actualValue == null && actualValue == null))
            return false;

        if (defaultValue != null && other.defaultValue != null)
            if (!defaultValue.equals(other.defaultValue)) return false;

        if (!(defaultValue == null && other.defaultValue != null))
            return false;

        if (description != null && other.description != null)
            return false;

        return other.description == null && description == null;
    }
}
