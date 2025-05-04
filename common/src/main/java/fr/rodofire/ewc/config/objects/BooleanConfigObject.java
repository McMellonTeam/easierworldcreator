package fr.rodofire.ewc.config.objects;


public class BooleanConfigObject extends AbstractConfigObject<Boolean> {

    public BooleanConfigObject(final boolean defaultValue, String name) {
        super(name, defaultValue);
    }

    public BooleanConfigObject(final boolean defaultValue, String description, String name) {
        super(description, name, defaultValue);
        this.actualValue = defaultValue;
    }

    @Override
    public String getObjectCategory() {
        return "Boolean";
    }

    @Override
    public String getDefaultDescription(String modId) {
        return super.getDefaultDescription(modId) + ("#\ttrue\n#\tfalse");
    }
}
