/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.canary.Canary;
import org.adamalang.common.ANSI;
import org.adamalang.cli.Config;
import org.adamalang.common.ColorUtilTools;
import org.adamalang.cli.devbox.DevBoxInputTranslator;
import org.adamalang.cli.implementations.space.Kickstarter;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.*;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.common.Platform;
import org.adamalang.devbox.Start;

import java.util.Map;

public class RootHandlerImpl implements RootHandler {
  @Override
  public SpaceHandler makeSpaceHandler() {
    return new SpaceHandlerImpl();
  }

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
  public OpsHandler makeOpsHandler() {
    return new OpsHandlerImpl();
  }

  @Override
  public void dumpenv(Arguments.DumpenvArgs args, Output.YesOrError output) throws Exception {
    for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
      System.err.println(entry.getKey() + "=" + entry.getValue());
    }
  }

  @Override
  public void version(Arguments.VersionArgs args, Output.YesOrError output) throws Exception {
    System.err.println("adama version " + ColorUtilTools.prefix(Platform.VERSION, ANSI.Green));
  }

  @Override
  public void init(Arguments.InitArgs args, Output.YesOrError output) throws Exception {
    System.out.println("Hello, and welcome to the " + ColorUtilTools.prefix("Adama Tool", ANSI.Green) + " account on-boarding experience!");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("WARNING!! ", ANSI.Red) + "Please be advised that this is alpha software available as an early release. For more details, please see:");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("  https://www.adama-platform.com/2022/03/02/early-access-launch-and-confession.html", ANSI.Green));
    System.out.println();
    System.out.println("Before you begin, you should read " + ColorUtilTools.prefix("terms & conditions", ANSI.Yellow) + " on the website:");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("  https://www.adama-platform.com/terms-and-conditions.html", ANSI.Green));
    System.out.println();
    System.out.println("Also, you should read the " + ColorUtilTools.prefix("privacy policy", ANSI.Yellow) + " on the website as well:");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("  https://www.adama-platform.com/privacy.html", ANSI.Green));
    System.out.println();
    System.out.println("This tool is about to ask for your email, and that is used as your developer account." + " As of March 3rd 2022 the email is only used for account verification, but we reserve the " + ColorUtilTools.prefix("option to use for updates in the future.", ANSI.Yellow) + " We will never share with third parties beyond tools to distribute updates about the service." + " Heck! we haven't yet written the code to dump emails into software which will handle that for us, and if we are successful enough with traction then we will not need to.");
    System.out.println();
    System.out.print("So, look, go tell your friends about how neat this software is and then we will be too busy with feature requests to even hire a marketing person.");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("As such, providing your email constitutes a binding agreement to both the privacy policy and our terms of conditions.", ANSI.Yellow));
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
        System.out.print(ColorUtilTools.prefix("Code:", ANSI.Yellow));
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

  private static String readEmail(Config config) {
    System.out.println();
    System.out.print(ColorUtilTools.prefix("Email:", ANSI.Yellow));
    String email = System.console().readLine();
    return email;
  }

  private static boolean revokePrior(Config config) {
    System.out.println();
    System.out.print(ColorUtilTools.prefix("Revoke other machines[y/N]:", ANSI.Yellow));
    String revokeYesNo = System.console().readLine();
    return revokeYesNo.trim().equalsIgnoreCase("Y");
  }

  @Override
  public void kickstart(Arguments.KickstartArgs args, Output.YesOrError output) throws Exception {
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        new Kickstarter(args.config, connection).flow();
      }
    }
  }

  private static String confirm(Config config) {
    System.out.println();
    System.out.print(ColorUtilTools.prefix("Are you sure? [Yes/No]:", ANSI.Yellow));
    return System.console().readLine().trim().toLowerCase();
  }

  @Override
  public void deinit(Arguments.DeinitArgs args, Output.YesOrError output) throws Exception {
    String email = args.config.get_string("email", "");
    if ("".equals(email)) {
      System.err.println("This tool has not been initialized with an email, please init again prior to deinit.");
      return;
    }
    System.out.println("Oh geez, we are sad to see you wanting to delete your account... :(");
    System.out.println();
    System.out.println("Now, before you deinit yourself, you need to delete your spaces, authorities, and domains or the command will fail. This is a safety mechanism to prevent massive data loss.");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("WARNING! ", ANSI.Red) + "This is going to delete the account for " + ColorUtilTools.prefix(email, ANSI.Cyan));
    System.out.println();
    String confirmation = confirm(args.config).trim();
    if ("yes".equals(confirmation)) {
      String identity = args.config.get_string("identity", null);
      try (WebSocketClient client = new WebSocketClient(args.config)) {
        try (Connection connection = client.open()) {
          ObjectNode request = Json.newJsonObject();
          request.put("method", "deinit");
          request.put("identity", identity);
          connection.execute(request);
          output.out();
        }
      }
    }
  }

  @Override
  public void canary(Arguments.CanaryArgs args, Output.YesOrError output) throws Exception {
    Canary.run(args.config.get_string("canary-endpoint", "wss://aws-us-east-2.adama-platform.com/~s"), args.scenario, (out) -> {
      System.out.println(out);
    });
  }

  @Override
  public void devbox(Arguments.DevboxArgs args, Output.YesOrError output) throws Exception {
    Start.start(DevBoxInputTranslator.from(args));
  }
}
