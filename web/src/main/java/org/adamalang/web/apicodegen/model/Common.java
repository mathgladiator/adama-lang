package org.adamalang.web.apicodegen.model;

import java.util.Locale;
import java.util.regex.Pattern;

public class Common {
    public static String camelize(String name) {
        String[] parts = name.split(Pattern.quote("-"));
        StringBuilder result = new StringBuilder();
        for  (String part : parts) {
            result.append(part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1).toLowerCase(Locale.ROOT));
        }
        return result.toString();
    }

}
