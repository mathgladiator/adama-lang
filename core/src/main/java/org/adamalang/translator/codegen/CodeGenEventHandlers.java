/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
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
    var askInventionCount = 0;
    var askSendWhileDisconnected = 0;
    for (final DefineDocumentEvent dce : environment.document.events) {
      String contextName = dce.contextVariable != null ? dce.contextVariable : "__context";
      if (dce.which == DocumentEvent.ClientConnected) {
        sb.append("public boolean __onConnected__" + connectCount + "(NtClient " + dce.clientVarToken.text + ") ");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        connectCount++;
      } else if (dce.which == DocumentEvent.ClientDisconnected) {
        sb.append("public void __onDisconnected__" + disconnectCount + "(NtClient " + dce.clientVarToken.text + ") ");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        disconnectCount++;
      } else if (dce.which == DocumentEvent.AskCreation) {
        sb.append("public static boolean __onCanCreate__" + askCreationCount + "(StaticState __static_state, NtClient " + dce.clientVarToken.text + ", CoreRequestContext " + contextName + ") ");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        askCreationCount++;
      } else if (dce.which == DocumentEvent.AskInvention) {
        sb.append("public static boolean __onCanInvent__" + askInventionCount + "(StaticState __static_state, NtClient " + dce.clientVarToken.text + ", CoreRequestContext " + contextName + ")");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        askInventionCount++;
      } else if (dce.which == DocumentEvent.AskSendWhileDisconnected) {
        sb.append("public static boolean __onCanSendWhileDisconnected__" + askSendWhileDisconnected + "(StaticState __static_state, NtClient " + dce.clientVarToken.text + ", CoreRequestContext " + contextName + ")");
        dce.code.writeJava(sb, dce.nextEnvironment(environment));
        askSendWhileDisconnected++;
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
    sb.append("public static boolean __onCanCreate(CoreRequestContext __context) {").tabUp().writeNewline();
    if (askCreationCount > 0) {
      sb.append("boolean __result = false;").writeNewline();
      sb.append("StaticState __static_state = new StaticState();").writeNewline();
      for (var k = 0; k < askCreationCount; k++) {
        sb.append("if (__onCanCreate__" + k + "(__static_state, __context.who, __context)) {").tabUp().writeNewline();
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

    // inject the can invent a topic when the document doesn't exist
    sb.append("public static boolean __onCanInvent(CoreRequestContext __context) {").tabUp().writeNewline();
    if (askInventionCount > 0) {
      sb.append("boolean __result = false;").writeNewline();
      sb.append("StaticState __static_state = new StaticState();").writeNewline();
      for (var k = 0; k < askInventionCount; k++) {
        sb.append("if (__onCanInvent__" + k + "(__static_state, __context.who, __context)) {").tabUp().writeNewline();
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

    // inject the can send to the document when it doesn't exist
    sb.append("public static boolean __onCanSendWhileDisconnected(CoreRequestContext __context) {").tabUp().writeNewline();
    if (askSendWhileDisconnected > 0) {
      sb.append("boolean __result = false;").writeNewline();
      sb.append("StaticState __static_state = new StaticState();").writeNewline();
      for (var k = 0; k < askSendWhileDisconnected; k++) {
        sb.append("if (__onCanSendWhileDisconnected__" + k + "(__static_state, __context.who, __context)) {").tabUp().writeNewline();
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
