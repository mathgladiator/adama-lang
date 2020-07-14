/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.codegen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineHandler;
import org.adamalang.translator.tree.definitions.MessageHandlerBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.traits.IsStructure;

/** responsible for writing all channel and message handlers */
public class CodeGenMessageHandling {
  public static void writeMessageHandlers(final StringBuilderWithTabs sb, final Environment environment) {
    final var dispatch = new StringBuilderWithTabs().tabUp().tabUp().tabUp();
    final var classFields = new StringBuilderWithTabs().tabUp();
    final var channelsPerMessageType = new LinkedHashMap<String, ArrayList<String>>();
    final var channelsPerMessageArrayType = new LinkedHashMap<String, ArrayList<String>>();
    final var channelsDefined = new HashSet<String>();
    final var resetFutureQueueBody = new StringBuilderWithTabs().tabUp().tabUp();
    for (final DefineHandler handler : environment.document.handlers) {
      if (channelsDefined.contains(handler.channel)) {
        environment.document.createError(handler, String.format("Channel '%s' is already handled", handler.channel), "MessageHandlers");
        continue;
      }
      channelsDefined.add(handler.channel);
      var clientVarToUse = "client";
      var payloadNameToUse = "payload";
      if (handler.client != null) {
        clientVarToUse = handler.client;
      }
      if (handler.messageVar != null) {
        payloadNameToUse = handler.messageVar;
      }
      final IsStructure rawStorageType = environment.rules.FindMessageStructure(handler.typeName, handler, false);
      if (rawStorageType != null && rawStorageType instanceof TyNativeMessage) {
        final var associatedRecordType = (TyNativeMessage) rawStorageType;
        if (handler.behavior == MessageHandlerBehavior.EnqueueItemIntoNativeChannel) {
          dispatch.append("case \"" + handler.channel + "\":").tabUp().writeNewline();
          dispatch.append("__queue_").append(handler.channel).append(".enqueue(task, ");
          if (handler.isArray) {
            dispatch.append("__BRIDGE_").append(handler.typeName).append(".convertArrayMessage");
          } else {
            dispatch.append("new RTx").append(handler.typeName);
          }
          dispatch.append("(task.message));").writeNewline();
          dispatch.append("return;").tabDown().writeNewline();
          final var mapToIndexIn = handler.isArray ? channelsPerMessageArrayType : channelsPerMessageType;
          var indexChannels = mapToIndexIn.get(handler.typeName);
          if (indexChannels == null) {
            indexChannels = new ArrayList<>();
            mapToIndexIn.put(handler.typeName, indexChannels);
          }
          indexChannels.add(handler.channel);
          classFields.append("private final Sink<RTx" + handler.typeName + "");
          if (handler.isArray) {
            classFields.append("[]");
          }
          classFields.append("> __queue_").append(handler.channel).append(" = new Sink<>(\"" + handler.channel + "\");").writeNewline();
          classFields.append("private final NtChannel<RTx" + handler.typeName + "");
          if (handler.isArray) {
            classFields.append("[]");
          }
          classFields.append("> ").append(handler.channel).append(" = new NtChannel<>(__futures, __queue_" + handler.channel + ");").writeNewline();
          resetFutureQueueBody.append("__queue_" + handler.channel + ".clear();").writeNewline();
        }
        if (handler.behavior == MessageHandlerBehavior.ExecuteAssociatedCode) {
          dispatch.append("case \"" + handler.channel + "\":").tabUp().writeNewline();
          if (handler.isArray) {
            dispatch.append("task.setAction(() -> handleChannelMessage_" + handler.channel + "(task.who, __BRIDGE_" + handler.typeName + ".convertArrayMessage(task.message)));").writeNewline();
          } else {
            dispatch.append("task.setAction(() -> handleChannelMessage_" + handler.channel + "(task.who, new RTx" + handler.typeName + "(task.message)));").writeNewline();
          }
          dispatch.append("return;").tabDown().writeNewline();
          final var child = handler.prepareEnv(environment, associatedRecordType);
          sb.append("private void handleChannelMessage_").append(handler.channel).append("(NtClient ").append(clientVarToUse).append(", RTx").append(handler.typeName);
          if (handler.isArray) {
            sb.append("[]");
          }
          sb.append(" ").append(payloadNameToUse).append(") throws AbortMessageException {").tabUp().writeNewline();
          handler.code.specialWriteJava(sb, child, false, false);
          sb.tabDown().writeNewline().append("}").writeNewline();
        }
      }
    }
    sb.append(classFields.toString());
    sb.append("@Override").writeNewline();
    if (environment.document.handlers.size() > 0) {
      sb.append("protected void __route(AsyncTask task) {").tabUp().writeNewline();
      sb.append("switch (task.channel) {").tabUp().writeNewline();
      sb.append(dispatch.toString());
      sb.append("default:").tabUp().writeNewline();
      sb.append("return;").tabDown().tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else {
      sb.append("protected void __route(AsyncTask task) {").tabUp().writeNewline();
      sb.append("return;").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
    sb.append("@Override").writeNewline();
    sb.append("protected void __reset_future_queues() {").writeNewline();
    final var commitBodyWrite = resetFutureQueueBody.toString().trim();
    if (commitBodyWrite.length() > 0) {
      sb.tab().append(commitBodyWrite).writeNewline();
    }
    sb.append("}").writeNewline();
  }
}
