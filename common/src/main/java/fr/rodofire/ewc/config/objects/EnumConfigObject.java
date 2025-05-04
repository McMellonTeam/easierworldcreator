package fr.rodofire.ewc.config.objects;


import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class EnumConfigObject extends AbstractConfigObject<String> {
    private final LinkedHashSet<String> enums = new LinkedHashSet<>();

    public EnumConfigObject(String defaultValue, String name, String description, Set<String> enums) {
        super(description, name, defaultValue);
        this.enums.addAll(enums);
    }


    public EnumConfigObject(String defaultValue, String name, Set<String> enums) {
        super(name, defaultValue);
        this.enums.addAll(enums);
    }

    public String getNext() {
        Iterator<String> iter = enums.iterator();
        int i = 0;
        String actual = iter.next();
        while (!actual.equals(actualValue)) {
            i++;
            actual = iter.next();
        }
        return i >= enums.size() - 1 ? enums.getFirst() : iter.next();
    }

    public LinkedHashSet<String> getEnums() {
        return enums;
    }

    @Override
    public String getObjectCategory() {
        return "Enums";
    }

    @Override
    public String getDefaultDescription(String modId) {
        return super.getDefaultDescription(modId) + ("#\t" + enums.toString());
    }

    public LinkedHashSet<String> getPossibleValues() {
        return enums;
    }
}
