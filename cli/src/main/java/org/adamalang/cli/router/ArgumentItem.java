package org.adamalang.cli.router;

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

    public ArgumentItem(String name, String shortField, String documentation, String defaultArg) {
        this.name = name;
        this.shortField = shortField;
        this.documentation = documentation;
        this.optional = true;
        this.defaultArg = defaultArg;
    }

    public ArgumentItem copy() {
        ArgumentItem returnArg = new ArgumentItem(name, shortField, documentation);
        returnArg.defaultArg = defaultArg;
        returnArg.optional = optional;
        return returnArg;
    }

}
