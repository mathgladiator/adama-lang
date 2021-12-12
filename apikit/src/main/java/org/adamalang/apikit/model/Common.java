package org.adamalang.apikit.model;

import java.util.Locale;
import java.util.regex.Pattern;

public class Common {
    public static String camelize(String name) {
        return camelize(name, false);
    }
    public static String camelize(String name, boolean lowerFirst) {
        String[] parts = name.replaceAll(Pattern.quote("/"), "-").split(Pattern.quote("-"));
        StringBuilder result = new StringBuilder();
        boolean lower = lowerFirst;
        for  (String part : parts) {
            if (lowerFirst) {
                result.append(part.toLowerCase(Locale.ROOT));
            } else {
                result.append(part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1).toLowerCase(Locale.ROOT));
            }
            lowerFirst = false;
        }
        return result.toString();
    }
}
