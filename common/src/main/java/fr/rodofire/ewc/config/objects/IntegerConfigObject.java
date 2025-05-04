package fr.rodofire.ewc.config.objects;

@SuppressWarnings("unused")
public class IntegerConfigObject extends AbstractConfigObject<Integer> {
    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;


    public IntegerConfigObject(final int defaultValue, String description, String name) {
        super(description, name, defaultValue);
    }

    public IntegerConfigObject(final int defaultValue, String name) {
        super(name, name, defaultValue);
    }

    public IntegerConfigObject(final int defaultValue, final int minValue, final int maxValue, String name, String description) {
        super(description, name, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public IntegerConfigObject(final int defaultValue, final int minValue, final int maxValue, String name) {
        super(name, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void setActualValue(Integer actualValue) {
        if (actualValue > maxValue) {
            super.setActualValue(maxValue);
        } else if (actualValue < minValue) {
            super.setActualValue(minValue);
        } else {
            super.setActualValue(actualValue);
        }
    }

    public int getMinValue() {
        return minValue;
    }

    public boolean isAcceptable(int value) {
        return value >= minValue && value <= maxValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String getObjectCategory() {
        return "Integers";
    }

    @Override
    public String getDefaultDescription(String modId) {
        return super.getDefaultDescription(modId) + ("#\tMin value: " + minValue + "\n#\tMax value: " + maxValue);
    }
}
