/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.DomainHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;

import java.io.File;
import java.nio.file.Files;

public class DomainHandlerImpl implements DomainHandler {
    @Override
    public void map(Arguments.DomainMapArgs args, Output.YesOrError output) throws Exception {
        String identity = args.config.get_string("identity", null);
        String autoStr = args.auto.toLowerCase();
        boolean automatic = "true".equals(autoStr) || "yes".equals(autoStr);
        final String cert;
        if (!automatic) {
            cert = Files.readString(new File(args.cert).toPath());
        } else {
            cert = null;
        }
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                if (args.key != null) {
                    request.put("method", "domain/map-document");
                    request.put("key", args.key);
                } else {
                    request.put("method", "domain/map");
                }
                request.put("identity", identity);
                request.put("domain", args.domain);
                request.put("space", args.space);
                if (cert != null) {
                    request.put("certificate", cert);
                }
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
    }

    @Override
    public void list(Arguments.DomainListArgs args, Output.JsonOrError output) throws Exception {
        String identity = args.config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "domain/list");
                request.put("identity", identity);
                connection.stream(request, (_k, item) -> {
                    System.err.println(item.toPrettyString());
                });
            }
        }
    }

    @Override
    public void unmap(Arguments.DomainUnmapArgs args, Output.YesOrError output) throws Exception {
        String identity = args.config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "domain/unmap");
                request.put("identity", identity);
                request.put("domain", args.domain);
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
    }
}
