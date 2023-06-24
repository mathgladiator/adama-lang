/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineAuthorization;
import org.adamalang.translator.tree.definitions.DefineHandler;
import org.adamalang.translator.tree.definitions.DefinePassword;

import java.util.ArrayList;

/** authenticate a username/pw pair to a agent under the document */
public class CodeGenAuth {
  public static void writeAuth(final StringBuilderWithTabs sb, Environment raw) {
    sb.append("@Override").writeNewline();
    sb.append("public boolean __open_channel(String name) {").tabUp().writeNewline();
    ArrayList<String> channels = new ArrayList<>();
    for (DefineHandler dh : raw.document.handlers) {
      if (dh.isOpen()) {
        channels.add(dh.channel);
      }
    }
    if (channels.size() == 1) {
      sb.append("return name.equals(\"").append(channels.get(0)).append("\");").tabDown().writeNewline();
    } else if (channels.size() > 1) {
      sb.append("switch (name) {").tabUp().writeNewline();
      int countDown = channels.size();
      for (String channel : channels) {
        sb.append("case \"").append(channel).append("\":");
        countDown--;
        if (countDown <= 0) sb.tabUp();
        sb.writeNewline();
      }
      sb.append("return true;").tabDown().writeNewline();
      sb.append("default:").tabUp().writeNewline();
      sb.append("return false;").tabDown().tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
    } else {
      sb.append("return false;").tabDown().writeNewline();
    }
    sb.append("}").writeNewline();

    sb.append("@Override").writeNewline();
    if (raw.document.auths.size() == 1) {
      DefineAuthorization authorization = raw.document.auths.get(0);
      Environment environment = authorization.next(raw);
      sb.append("public String __auth(CoreRequestContext __context, String ").append(authorization.username.text).append(", String ").append(authorization.password.text).append(") {").tabUp().writeNewline();
      sb.append("try {").tabUp().writeNewline();
      sb.append("if (").append(authorization.username.text).append("== null && null == ").append(authorization.password.text).append(") throw new AbortMessageException();").writeNewline();
      authorization.code.specialWriteJava(sb, environment, false, true);
      sb.append("} catch (AbortMessageException ame) {").tabUp().writeNewline();
      sb.append("return null;").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
    } else {
      sb.append("public String __auth(CoreRequestContext __context, String username, String password) {").tabUp().writeNewline();
      sb.append("return null;").tabDown().writeNewline();
    }
    sb.append("}").writeNewline();

    sb.append("@Override").writeNewline();
    if (raw.document.passwords.size() == 1) {
      DefinePassword dp = raw.document.passwords.get(0);
      Environment environment = dp.next(raw);
      sb.append("public void __password(CoreRequestContext __context, String ").append(dp.passwordVar.text).append(") {").tabUp().writeNewline();
      sb.append("NtPrincipal __who = __context.who;").writeNewline();
      dp.code.specialWriteJava(sb, environment, false, true);
    } else {
      sb.append("public void __password(CoreRequestContext __context, String __pw) {");
    }
    sb.append("}").writeNewline();
  }
}
