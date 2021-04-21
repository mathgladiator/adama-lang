/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
    final var parser2 = new StringBuilderWithTabs().tabUp().tabUp().tabUp();
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
        parser2.append("case \"" + handler.channel + "\":").tabUp().writeNewline();
        if (handler.isArray) {
          parser2.append("{").tabUp().writeNewline();
          parser2.append("ArrayList<RTx").append(handler.typeName).append("> __array_").append(handler.channel).append(" = new ArrayList<>();").writeNewline();
          parser2.append("if (__reader.startArray()) {").tabUp().writeNewline();
          parser2.append("while (__reader.notEndOfArray()) {").tabUp().writeNewline();
          parser2.append("__array_").append(handler.channel).append(".add(new RTx").append(handler.typeName).append("(__reader));").tabDown().writeNewline();
          parser2.append("}").tabDown().writeNewline();
          parser2.append("}").writeNewline();
          parser2.append("return __array_").append(handler.channel).append(".toArray(new RTx").append(handler.typeName).append("[__array_").append(handler.channel).append(".size()]);").tabDown().writeNewline();
          parser2.append("}").writeNewline();
        } else {
          parser2.append("return new RTx").append(handler.typeName).append("(__reader);");
        }
        parser2.tabDown().writeNewline();
        if (handler.behavior == MessageHandlerBehavior.EnqueueItemIntoNativeChannel) {
          dispatch.append("case \"").append(handler.channel).append("\":").tabUp().writeNewline();
          dispatch.append("__queue_").append(handler.channel).append(".enqueue(__task, ");
          dispatch.append("(RTx").append(handler.typeName);
          if (handler.isArray) {
            dispatch.append("[]");
          }
          dispatch.append(") __task.message);").writeNewline();
          dispatch.append("return;").tabDown().writeNewline();
          final var mapToIndexIn = handler.isArray ? channelsPerMessageArrayType : channelsPerMessageType;
          var indexChannels = mapToIndexIn.get(handler.typeName);
          if (indexChannels == null) {
            indexChannels = new ArrayList<>();
            mapToIndexIn.put(handler.typeName, indexChannels);
          }
          indexChannels.add(handler.channel);
          classFields.append("private final Sink<RTx").append(handler.typeName);
          if (handler.isArray) {
            classFields.append("[]");
          }
          classFields.append("> __queue_").append(handler.channel).append(" = new Sink<>(\"").append(handler.channel).append("\");").writeNewline();
          classFields.append("private final NtChannel<RTx" + handler.typeName + "");
          if (handler.isArray) {
            classFields.append("[]");
          }
          classFields.append("> ").append(handler.channel).append(" = new NtChannel<>(__futures, __queue_" + handler.channel + ");").writeNewline();
          resetFutureQueueBody.append("__queue_" + handler.channel + ".clear();").writeNewline();
        }
        if (handler.behavior == MessageHandlerBehavior.ExecuteAssociatedCode) {
          dispatch.append("case \"").append(handler.channel).append("\":").tabUp().writeNewline();
          dispatch.append("__task.setAction(() -> handleChannelMessage_").append(handler.channel).append("(__task.who, (RTx").append(handler.typeName);
          if (handler.isArray) {
            dispatch.append("[]");
          }
          dispatch.append(")(__task.message)));").writeNewline();
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
    if (environment.document.handlers.size() > 0) {
      sb.append("@Override").writeNewline();
      sb.append("protected void __route(AsyncTask __task) {").tabUp().writeNewline();
      sb.append("switch (__task.channel) {").tabUp().writeNewline();
      sb.append(dispatch.toString());
      sb.append("default:").tabUp().writeNewline();
      sb.append("return;").tabDown().tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
      sb.append("}").writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("protected Object __parse_message2(String __channel, JsonStreamReader __reader) {").tabUp().writeNewline();
      sb.append("switch (__channel) {").tabUp().writeNewline();
      sb.append(parser2.toString());
      sb.append("default:").tabUp().writeNewline();
      sb.append("__reader.skipValue();").writeNewline();
      sb.append("return NtMessageBase.NULL;").tabDown().tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else {
      sb.append("@Override").writeNewline();
      sb.append("protected void __route(AsyncTask task) {").tabUp().writeNewline();
      sb.append("return;").tabDown().writeNewline();
      sb.append("}").writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("protected Object __parse_message2(String channel, JsonStreamReader __reader) {").tabUp().writeNewline();
      sb.append("__reader.skipValue();").writeNewline();
      sb.append("return NtMessageBase.NULL;").tabDown().writeNewline();
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
