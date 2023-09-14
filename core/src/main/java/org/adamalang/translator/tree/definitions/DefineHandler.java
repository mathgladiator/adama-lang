/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.privacy.Guard;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;
import org.adamalang.translator.tree.types.natives.TyNativeArray;
import org.adamalang.translator.tree.types.natives.TyNativeChannel;
import org.adamalang.translator.tree.types.traits.IsStructure;

import java.util.function.Consumer;

/**
 * A handler is called when the document receives a message from a client. There are a variety of
 * actions the document can take. For instance, the document could simply drop it. These behaviors
 * are explain in the MessageHandlerBehavior.
 */
public class DefineHandler extends Definition {
  public final String channel;
  private final Token channelNameToken;
  private final Token channelToken;
  public MessageHandlerBehavior behavior;
  public Block code;
  public boolean isArray;
  public String messageVar;
  public String typeName;
  private Token endParenToken = null;
  private Token endType = null;
  private Token messageTypeArrayToken = null;
  private Token messageTypeToken = null;
  private Token messageVarToken = null;
  private Token openParenToken = null;
  private Token isOpen = null;
  private Token openType = null;
  private Token semicolonToken = null;
  private Token requires = null;
  public Guard guard = null;

  public DefineHandler(final Token channelToken, final Token channelNameToken) {
    this.channelToken = channelToken;
    this.channelNameToken = channelNameToken;
    channel = channelNameToken.text;
    typeName = null;
    behavior = null;
    messageVar = null;
    isArray = false;
    ingest(channelToken);
    ingest(channelNameToken);
  }

  public void setGuard(Token requires, Guard guard) {
    this.requires = requires;
    this.guard = guard;
    ingest(guard);
  }

  public boolean isOpen() {
    return isOpen != null;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (openType != null) {
      yielder.accept(channelToken);
      yielder.accept(openType);
      yielder.accept(messageTypeToken);
      if (messageTypeArrayToken != null) {
        yielder.accept(messageTypeArrayToken);
      }
      yielder.accept(endType);
      yielder.accept(channelNameToken);
      yielder.accept(semicolonToken);
      return;
    }
    yielder.accept(channelToken);
    yielder.accept(channelNameToken);
    if (openParenToken != null) {
      yielder.accept(openParenToken);
      yielder.accept(messageTypeToken);
      if (messageTypeArrayToken != null) {
        yielder.accept(messageTypeArrayToken);
      }
      yielder.accept(messageVarToken);
      yielder.accept(endParenToken);
      if (isOpen != null) {
        yielder.accept(isOpen);
      }
      if (requires != null) {
        yielder.accept(requires);
        guard.emit(yielder);
      }
      if (code != null) {
        code.emit(yielder);
      }
    }
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    if (messageVarToken != null) {
      fe.define(messageVarToken.text);
    }
    if (code != null) {
      code.free(fe);
    }
    checker.register(fe.free, (environment) -> {
      final IsStructure messageType = environment.rules.FindMessageStructure(typeName, this, false);
      if (messageType == null) {
        return;
      }
      final var next = prepareEnv(environment, messageType);
      if (code != null) {
        code.typing(next);
      }
      if (behavior == MessageHandlerBehavior.EnqueueItemIntoNativeChannel) {
        final var nativeChannel = new TyNativeChannel(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(isArray ? new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, (TyType) messageType, null) : (TyType) messageType)).withPosition(this);
        environment.define(channel, nativeChannel, false, nativeChannel);
      }
      if (guard != null) {
        for (TokenizedItem<String> policy : guard.policies) {
          if (environment.document.root.storage.policies.get(policy.item) == null) {
            environment.document.createError(this, String.format("Policy '%s' was not found for handler requirement", policy.item));
          }
        }
      }
    });
  }

  public Environment prepareEnv(final Environment environment, final IsStructure messageType) {
    final var next = environment.scopeAsMessageHandler();
    if (messageVar != null) {
      if (isArray) {
        next.define(messageVar, new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, (TyType) messageType, messageTypeArrayToken), true, this);
      } else {
        next.define(messageVar, (TyType) messageType, true, this);
      }
    }
    return next;
  }

  /** make the handler operate on arrays */
  public DefineHandler makeArray() {
    isArray = true;
    return this;
  }

  public void setFuture(final Token openType, final Token messageTypeToken, final Token messageTypeArrayToken, final Token endType, final Token semicolonToken) {
    this.openType = openType;
    this.messageTypeToken = messageTypeToken;
    this.messageTypeArrayToken = messageTypeArrayToken;
    this.endType = endType;
    this.semicolonToken = semicolonToken;
    behavior = MessageHandlerBehavior.EnqueueItemIntoNativeChannel;
    typeName = this.messageTypeToken.text;
    if (this.messageTypeArrayToken != null) {
      makeArray();
    }
    ingest(openType);
    ingest(messageTypeToken);
    ingest(semicolonToken);
  }

  public void setMessageOnlyHandler(final Token openParenToken, final Token messageTypeToken, final Token messageTypeArrayToken, final Token messageVarToken, final Token endParenToken, final Token isOpen, final Block code) {
    this.openParenToken = openParenToken;
    this.messageTypeToken = messageTypeToken;
    this.messageTypeArrayToken = messageTypeArrayToken;
    this.messageVarToken = messageVarToken;
    this.endParenToken = endParenToken;
    this.isOpen = isOpen;
    typeName = this.messageTypeToken.text;
    messageVar = this.messageVarToken.text;
    if (this.messageTypeArrayToken != null) {
      makeArray();
    }
    behavior = MessageHandlerBehavior.ExecuteAssociatedCode;
    this.code = code;
    ingest(code);
  }
}
