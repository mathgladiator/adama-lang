/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
    var askAssetAttachCount = 0;
    var askCreationCount = 0;
    for (final DefineDocumentEvent dce : environment.document.connectionEvents) {
      if (dce.which == DocumentEvent.ClientConnected) {
        sb.append("public boolean __onConnected__" + connectCount + "(NtClient " + dce.clientVarToken.text + ")");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        connectCount++;
      } else if (dce.which == DocumentEvent.ClientDisconnected) {
        sb.append("public void __onDisconnected__" + disconnectCount + "(NtClient " + dce.clientVarToken.text + ") ");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        disconnectCount++;
      } else if (dce.which == DocumentEvent.AskCreation) {
        sb.append("public static boolean __onCanCreate__" + askCreationCount + "(NtClient " + dce.clientVarToken.text + ", NtCreateContext " + dce.parameterNameToken.text + ") ");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        askCreationCount++;
      } else if (dce.which == DocumentEvent.AskAssetAttachment) {
        sb.append("public boolean __onCanAssetAttached__" + askAssetAttachCount + "(NtClient " + dce.clientVarToken.text + ") ");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        askAssetAttachCount++;
      } else if (dce.which == DocumentEvent.AssetAttachment) {
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

    // join the can asset attachment handlers into one
    sb.append("@Override").writeNewline();
    sb.append("public boolean __onCanAssetAttached(NtClient __cvalue) {").tabUp().writeNewline();
    if (askAssetAttachCount > 0) {
      sb.append("boolean __result = false;").writeNewline();
      for (var k = 0; k < askAssetAttachCount; k++) {
        sb.append("if (__onCanAssetAttached__" + k + "(__cvalue)) __result = true;").writeNewline();
      }
      sb.append("return __result;");
    } else {
      sb.append("return false;");
    }
    sb.tabDown().writeNewline();
    sb.append("}").writeNewline();

    // inject the can create policy
    sb.append("public static boolean __onCanCreate(NtClient __client, NtCreateContext __context) {").tabUp().writeNewline();
    if (askCreationCount > 0) {
      sb.append("boolean __result = false;").writeNewline();
      for (var k = 0; k < askCreationCount; k++) {
        sb.append("if (__onCanCreate__" + k + "(__client, __context)) {").tabUp().writeNewline();
        sb.append("__result = true;").tabDown().writeNewline();
        sb.append("} else {").tabUp().writeNewline();
        sb.append("return false;").tabDown().writeNewline();
        sb.append("}");
      }
      sb.append("return __result;");
    } else {
      sb.append("return false;");
    }
    sb.tabDown().writeNewline();
    sb.append("}").writeNewline();

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
