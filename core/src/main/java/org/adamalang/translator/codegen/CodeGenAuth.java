/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineAuthorization;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeString;

/** authenticate a username/pw pair to a agent under the document */
public class CodeGenAuth {
  public static void writeAuth(final StringBuilderWithTabs sb, Environment raw) {
    sb.append("@Override").writeNewline();
    if (raw.document.auths.size() == 1) {
      DefineAuthorization authorization = raw.document.auths.get(0);
      Environment environment = authorization.next(raw);
      sb.append("public String __auth(String ").append(authorization.username.text).append(", String ").append(authorization.password.text).append(") {").tabUp().writeNewline();
      TyNativeString tyStr = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, authorization.authorize);
      environment.define(authorization.username.text, tyStr, true, authorization);
      environment.define(authorization.password.text, tyStr, true, authorization);
      sb.append("try {").tabUp().writeNewline();
      authorization.code.specialWriteJava(sb, environment, false, true);
      sb.append("} catch (AbortMessageException ame) {").tabUp().writeNewline();
      sb.append("return null;").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
    } else {
      sb.append("public String __auth(String username, String password) {").tabUp().writeNewline();
      sb.append("return null;").tabDown().writeNewline();
    }
    sb.append("}").writeNewline();
  }
}
