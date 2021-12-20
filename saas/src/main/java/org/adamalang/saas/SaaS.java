package org.adamalang.saas;

public class SaaS {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("requires args");
            System.exit(1);
        }
        if ("frontend".equals(args[0])) {
            Frontend.execute();
        }
    }
}
