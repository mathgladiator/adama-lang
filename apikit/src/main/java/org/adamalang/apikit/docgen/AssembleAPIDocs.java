package org.adamalang.apikit.docgen;

import org.adamalang.apikit.model.Method;

import java.util.regex.Pattern;

public class AssembleAPIDocs {

    public static String docify(Method[] methods) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# API Reference \n");
        markdown.append("\n");

        for (Method method : methods) {
            markdown.append("## Method: ").append(method.name).append("\n");
            for (String ln : method.documentation.trim().split(Pattern.quote("\n"))) {
                markdown.append(ln.trim()).append("\n");
            }
            markdown.append("\n");
        }

        return markdown.toString();
    }
}
