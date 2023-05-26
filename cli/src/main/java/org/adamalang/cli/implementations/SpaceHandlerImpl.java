package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.ArgumentType;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.router.SpaceHandler;
import org.adamalang.common.Json;
import org.adamalang.common.Validators;

import java.util.ArrayList;

public class SpaceHandlerImpl implements SpaceHandler {

    @Override
    public int createSpace(ArgumentType.CreateSpaceArgs args, Output output) {
        Config config;
        try {
            config = new Config(new String[0]);
        } catch (Exception e) {
            return 1;
        }

        String identity = config.get_string("identity", null);
        if (!Validators.simple(args.space, 127)) {
            System.err.println("Space name `" + args.space + "` is not valid");
            return 1;
        }
        try (WebSocketClient client = new WebSocketClient(config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/create");
                request.put("identity", identity);
                request.put("space", args.space);
                ObjectNode response = connection.execute(request);

                System.err.println(response.toPrettyString());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteSpace(ArgumentType.DeleteSpaceArgs args, Output output) {
        return 0;
    }

    @Override
    public int deploySpace(ArgumentType.DeploySpaceArgs args, Output output) {
        return 0;
    }

    @Override
    public int setRxhtmlSpace(ArgumentType.SetRxhtmlSpaceArgs args, Output output) {
        return 0;
    }

    @Override
    public int uploadSpace(ArgumentType.UploadSpaceArgs args, Output output) {
        return 0;
    }

    @Override
    public int downloadSpace(ArgumentType.DownloadSpaceArgs args, Output output) {
        return 0;
    }

    @Override
    public int listSpace(ArgumentType.ListSpaceArgs args, Output output){
        Config config;
        try {
            config = new Config(new String[0]);
        } catch (Exception e) {
            return 1;
        }

        String identity = config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/list");
                request.put("identity", identity);
                if (!"".equals(args.marker)) {
                    request.put("marker", args.marker);
                }
                request.put("limit", Integer.parseInt(args.limit));
                ArrayList<ObjectNode> nodes = new ArrayList<>();
                connection.stream(request, (cId, response) -> {
                    output.add(response);
                });
                output.out();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 1;
        }
        return 0;
    }

    @Override
    public int usageSpace(ArgumentType.UsageSpaceArgs args, Output output) {
        return 0;
    }

    @Override
    public int reflectSpace(ArgumentType.ReflectSpaceArgs args, Output output) {
        return 0;
    }

    @Override
    public int setRoleSpace(ArgumentType.SetRoleSpaceArgs args, Output output) {
        return 0;
    }

    @Override
    public int generateKeySpace(ArgumentType.GenerateKeySpaceArgs args, Output output) {
        return 0;
    }

    @Override
    public int encryptSecretSpace(ArgumentType.EncryptSecretSpaceArgs args, Output output) {
        return 0;
    }
}
