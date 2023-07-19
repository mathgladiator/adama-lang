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
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.*;
import org.adamalang.cli.runtime.Output;

import org.adamalang.common.Json;

import java.util.Map;

public class RootHandlerImpl implements RootHandler {
    @Override
    public AuthorityHandler makeAuthorityHandler() {
        return new AuthorityHandlerImpl();
    }

    @Override
    public AccountHandler makeAccountHandler() {
        return new AccountHandlerImpl();
    }

    @Override
    public CodeHandler makeCodeHandler() {
        return new CodeHandlerImpl();
    }

    @Override
    public ContribHandler makeContribHandler() {
        return new ContribHandlerImpl();
    }

    @Override
    public DatabaseHandler makeDatabaseHandler() {
        return new DatabaseHandlerImpl();
    }

    @Override
    public DocumentHandler makeDocumentHandler() {
        return new DocumentHandlerImpl();
    }

    @Override
    public DomainHandler makeDomainHandler() {
        return new DomainHandlerImpl();
    }

    @Override
    public FrontendHandler makeFrontendHandler() {
        return new FrontendHandlerImpl();
    }

    @Override
    public ServicesHandler makeServicesHandler() {
        return new ServicesHandlerImpl();
    }

    @Override
    public SpaceHandler makeSpaceHandler() {
        return new SpaceHandlerImpl();
    }

    private static String readEmail(Config config) {
        System.out.println();
        System.out.print(Util.prefix("Email:", Util.ANSI.Yellow));
        String email = System.console().readLine();
        return email;
    }

    private static boolean revokePrior(Config config) {
        System.out.println();
        System.out.print(Util.prefix("Revoke other machines[y/N]:", Util.ANSI.Yellow));
        String revokeYesNo = System.console().readLine();
        return revokeYesNo.trim().equalsIgnoreCase("Y");
    }

    @Override
    public void init(Arguments.InitArgs args, Output.YesOrError output) throws Exception {
        System.out.println("Hello, and welcome to the " + Util.prefix("Adama Tool", Util.ANSI.Green) + " account on-boarding experience!");
        System.out.println("");
        System.out.println(Util.prefix("WARNING!! ", Util.ANSI.Red) + "Please be advised that this is alpha software available as an early release. For more details, please see:");
        System.out.println("");
        System.out.println(Util.prefix("  https://www.adama-platform.com/2022/02/25/early-access-launch-and-confession.html", Util.ANSI.Green));
        System.out.println("");
        System.out.println("Before you begin, you should read " + Util.prefix("terms & conditions", Util.ANSI.Yellow) + " on the website:");
        System.out.println();
        System.out.println(Util.prefix("  https://www.adama-platform.com/terms-and-conditions.html", Util.ANSI.Green));
        System.out.println();
        System.out.println("Also, you should read the " + Util.prefix("privacy policy", Util.ANSI.Yellow) + " on the website as well:");
        System.out.println();
        System.out.println(Util.prefix("  https://www.adama-platform.com/privacy.html", Util.ANSI.Green));
        System.out.println();
        System.out.println("This tool is about to ask for your email, and that is used as your developer account." +
                " As of March 3rd 2022 the email is only used for account verification, but we reserve the " + Util.prefix("option to use for updates in the future.", Util.ANSI.Yellow) +
                " We will never share with third parties beyond tools to distribute updates about the service." +
                " Heck! we haven't yet written the code to dump emails into software which will handle that for us, and if we are successful enough with traction then we will not need to.");
        System.out.println();
        System.out.print("So, look, go tell your friends about how neat this software is and then we will be too busy with feature requests to even hire a marketing person.");
        System.out.println();
        System.out.println(Util.prefix("As such, providing your email constitutes a binding agreement to both the privacy policy and our terms of conditions.", Util.ANSI.Yellow));
        System.out.println();
        String email = readEmail(args.config);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode requestStart = Json.newJsonObject();
                requestStart.put("method", "init/setup-account");
                requestStart.put("email", email);
                connection.execute(requestStart);

                System.out.println();
                System.out.println("At this point, an email has been sent to the address that you just provided. So, go find the email (remember to check spam) and copy and paste the code into this prompt.");
                System.out.println();
                System.out.print(Util.prefix("Code:", Util.ANSI.Yellow));
                String code = System.console().readLine();

                ObjectNode requestGenerateIdentity = Json.newJsonObject();
                requestGenerateIdentity.put("method", "init/complete-account");
                requestGenerateIdentity.put("email", email);
                if (revokePrior(args.config)) {
                    requestGenerateIdentity.put("revoke", true);
                }
                requestGenerateIdentity.put("code", code);
                ObjectNode responseGenerateIdentity = connection.execute(requestGenerateIdentity);
                args.config.manipulate((node) -> {
                    node.put("email", email);
                    node.set("identity", responseGenerateIdentity.get("identity"));
                });
                output.out();
            }
        }
    }

    @Override
    public void dumpenv(Arguments.DumpenvArgs args, Output.YesOrError output) throws Exception {
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            System.err.println(entry.getKey() + "=" + entry.getValue());
        }
    }
}
