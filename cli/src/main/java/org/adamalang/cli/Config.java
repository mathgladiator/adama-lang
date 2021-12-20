package org.adamalang.cli;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Config {
    public final String[] argsForTool;
    public final String configPath;
    public final boolean requestingHelp;

    public Config(String[] args) throws Exception {
        ArrayList<String> argsToUse = new ArrayList<>();
        String _configPath = System.getProperty("user.home") + "/.adama";
        boolean _requestingHelp = false;
        for (int k = 0; k < args.length; k++) {
            if ("--config".equals(args[k]) && k + 1 < args.length) {
                _configPath = args[k + 1];
                k++;
                break;
            } else if ("--help".equals(args[k]) || "-h".equals(args[k])) {
                _requestingHelp = true;
                break;
            } else {
                argsToUse.add(args[k]);
            }
        }
        this.argsForTool = argsToUse.toArray(new String[argsToUse.size()]);
        this.configPath = _configPath;
        this.requestingHelp = _requestingHelp;
        File _configFile = new File(configPath);
        if (!_configFile.exists()) {
            ObjectNode defaultConfig = Util.newJsonObject();
            // TODO: once I have a launch, make this use the default URL and ideal parameters
            Files.writeString(_configFile.toPath(), defaultConfig.toPrettyString());
        }

        ObjectNode config = Util.parseJsonObject(Files.readString(_configFile.toPath()));
        // TODO: fun things
    }

    public void manipulate(Consumer<ObjectNode> manipulator) throws Exception {
        File _configFile = new File(configPath);
        ObjectNode config = Util.parseJsonObject(Files.readString(_configFile.toPath()));
        manipulator.accept(config);
        Files.writeString(_configFile.toPath(), config.toPrettyString());
    }
}
