/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineDocumentEvent;
import org.adamalang.translator.tree.definitions.DocumentEvent;

/** responsible for writing event handlers */
public class CodeGenEventHandlers {
  public static void writeEventHandlers(final StringBuilderWithTabs sb, final Environment environment) {
    // there can be multiple connected and disconnected handlers, we iterate them
    var connectCount = 0;
    var disconnectCount = 0;
    var assetAttachCount = 0;
    for (final DefineDocumentEvent dce : environment.document.connectionEvents) {
      if (dce.which == DocumentEvent.ClientConnected) {
        sb.append("public boolean __onConnected__" + connectCount + "(NtClient " + dce.clientVarToken.text + ")");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        connectCount++;
      } else if (dce.which == DocumentEvent.ClientDisconnected) {
        sb.append("public void __onDisconnected__" + disconnectCount + "(NtClient " + dce.clientVarToken.text + ") ");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        disconnectCount++;
      } else {
        sb.append("public void __onAssetAttached__" + disconnectCount + "(NtClient " + dce.clientVarToken.text + ", NtAsset " + dce.parameterNameToken.text + ") ");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        assetAttachCount++;

      }
      sb.writeNewline();
    }
    // join the connected handlers into one
    sb.append("@Override").writeNewline();
    sb.append("public boolean __onConnected(NtClient __cvalue) {").tabUp().writeNewline();
    sb.append("boolean __result = false;").writeNewline();
    for (var k = 0; k < connectCount; k++) {
      sb.append("if (__onConnected__" + k + "(__cvalue)) __result = true;").writeNewline();
    }
    sb.append("return __result;");
    sb.tabDown().writeNewline();
    sb.append("}").writeNewline();

    // join the disconnected handlers into one
    sb.append("@Override").writeNewline();
    if (disconnectCount == 0) {
      sb.append("public void __onDisconnected(NtClient __cvalue) {}").writeNewline();
    } else {
      sb.append("public void __onDisconnected(NtClient __cvalue) {").tabUp().writeNewline();
      for (var k = 0; k < disconnectCount; k++) {
        sb.append("__onDisconnected__" + k + "(__cvalue);");
        if (k == disconnectCount - 1) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").writeNewline();
    }

    // join the disconnected handlers into one
    sb.append("@Override").writeNewline();
    if (assetAttachCount == 0) {
      sb.append("public void __onAssetAttached(NtClient __cvalue, NtAsset __asset) {}").writeNewline();
    } else {
      sb.append("public void __onAssetAttached(NtClient __cvalue, NtAsset __asset) {").tabUp().writeNewline();
      for (var k = 0; k < assetAttachCount; k++) {
        sb.append("__onAssetAttached__" + k + "(__cvalue, __asset);");
        if (k == assetAttachCount - 1) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").writeNewline();
    }
  }
}
