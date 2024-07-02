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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineAuthorization;
import org.adamalang.translator.tree.definitions.DefineAuthorizationPipe;
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

    if (raw.document.authPipes.size() == 1) {
      sb.append("public AuthResponse __authpipe(CoreRequestContext __context, String __message) {").tabUp().writeNewline();
      DefineAuthorizationPipe pipe = raw.document.authPipes.get(0);
      sb.append("try {").tabUp().writeNewline();
      sb.append("if (__message == null) throw new AbortMessageException();").writeNewline();
      sb.append("RTx").append(pipe.messageType.text).append(" ").append(pipe.messageValue.text).append(" = new RTx").append(pipe.messageType.text).append("(new JsonStreamReader(__message));").writeNewline();
      pipe.code.specialWriteJava(sb, raw, false, true);
      sb.append("} catch (AbortMessageException ame) {").tabUp().writeNewline();
      sb.append("return null;").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
    } else {
      sb.append("public AuthResponse __authpipe(CoreRequestContext __context, String __message) {").tabUp().writeNewline();
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
