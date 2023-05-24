package org.adamalang.cli.router;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Output {
    public static class FileOutput {
        public void out(File fileOut, String content) {
            // Create file
            boolean creating = fileOut.exists();

            if (creating) {
                System.out.println("Writing to file " + fileOut);
            } else {
                System.out.println("Creating file " + fileOut);
            }
            try {
                Files.writeString(fileOut.toPath(), content);
            } catch (IOException exception) {
                System.err.println("Error " + (creating ? "creating" : "writing") + " file " + fileOut);
            }
        }

        public void err(String string) {
            System.err.println(string);
        }
    }

    public static class JsonOutput {
        public void out(ObjectNode jsonOut) {
            System.out.println(jsonOut.toPrettyString());
        }

        public void err(ObjectNode jsonOut) {
            System.err.println(jsonOut.toPrettyString());
        }
    }

    public static class AnsiOutput {
        public void out(String output) {
            System.out.println(output);
        }

        public void err(String output) {
            System.out.println(output);
        }
    }
}
