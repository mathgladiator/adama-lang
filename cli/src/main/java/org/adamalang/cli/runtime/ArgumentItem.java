package org.adamalang.cli.runtime;

import java.util.Map;

public class ArgumentItem {
    public String value;
    public String name;
    public String shortField;
    public String documentation;
    public String defaultArg = "";
    public boolean optional = false;

    public ArgumentItem(String name, String shortField, String documentation) {
        this.name = name;
        this.shortField = shortField;
        this.documentation = documentation;
    }

    public ArgumentItem copy() {
        ArgumentItem returnArg = new ArgumentItem(name, shortField, documentation);
        returnArg.defaultArg = defaultArg;
        returnArg.optional = optional;
        return returnArg;
    }

    public static ArgumentItem setOptionalFromMap(Map<String, ArgumentItem> map, String name, String defaultArg) {
        ArgumentItem returnArgItem = map.get(name);
        returnArgItem.defaultArg = defaultArg;
        returnArgItem.optional = true;
        return returnArgItem;
    }
}
