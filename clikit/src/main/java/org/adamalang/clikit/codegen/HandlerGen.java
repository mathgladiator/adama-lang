package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

import java.util.Map;
import java.util.TreeMap;

public class HandlerGen {


    public static Map<String, String> generate(Group[] groupList, String packageName) {
        Map<String, String> generatedMap = new TreeMap<>();
        for (Group group: groupList) {
            StringBuilder sb = new StringBuilder();

            sb.append("package " + packageName + ";\n\n");
            //Import required imports

            //Create interface
            sb.append("public interface " + group.capName + "Handler() {\n\n");
            //Implement both the help and router


            for (Command command : group.commandList) {
                String combName = command.name + group.capName;
                sb.append("  int " + combName + "(ArgumentObj." + combName + "Args args, YesNoOutput output);\n");
            }
            sb.append("}");

            generatedMap.put(group.capName+"Handler.java", sb.toString());

        }
        return generatedMap;


    }
}
