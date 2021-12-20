package org.adamalang.cli;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Playful {
    public static void main(String[] args) throws Exception {
        Config config = new Config(args);
        WebSocketClient socket = new WebSocketClient(config);
        WebSocketClient.Connection connection = socket.open();
        ObjectNode response = connection.requestResponse(Util.parseJsonObject("{\"method\":\"space/create\",\"identity\":\"jeff\",\"space\":\"s\"}"));
        System.err.println(response.toPrettyString());
    }
}
