/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.privacy.PublicPolicy;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;
import org.adamalang.translator.tree.types.natives.TyNativePrincipal;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.structures.StructureStorage;

import java.util.List;
import java.util.function.Consumer;

public class DefineRPC extends Definition {
  public final Token rpcToken;
  public final Token name;
  public final Token openParen;
  public final Token clientVar;
  public final List<FunctionArg> args;
  public final Token closeParen;
  public final Block code;
  private TyNativeMessage genMessageType;

  public DefineRPC(Token rpcToken, Token name, Token openParen, Token clientVar, List<FunctionArg> args, Token closeParen, Block code) {
    this.rpcToken = rpcToken;
    this.name = name;
    this.openParen = openParen;
    this.clientVar = clientVar;
    this.args = args;
    this.closeParen = closeParen;
    this.code = code;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(rpcToken);
    yielder.accept(name);
    yielder.accept(openParen);
    yielder.accept(clientVar);
    for (FunctionArg arg : args) {
      yielder.accept(arg.commaToken);
      arg.type.emit(yielder);
      yielder.accept(arg.argNameToken);
    }
    yielder.accept(closeParen);
    code.emit(yielder);
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    for (FunctionArg arg : args) {
      fe.define(arg.argNameToken.text);
    }
    code.free(fe);
    checker.register(fe.free, (environment) -> {
      final var next = environment.scopeAsMessageHandler();
      next.define(clientVar.text, new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, clientVar).withPosition(this), true, this);
      for (final FunctionArg arg : args) {
        next.define(arg.argName, arg.type, true, arg.type);
      }
      code.typing(next);
      genTyNativeMessage().typing(environment);
    });
  }

  public TyNativeMessage genTyNativeMessage() {
    if (genMessageType != null) {
      return genMessageType;
    }
    StructureStorage storage = new StructureStorage(StorageSpecialization.Message, false, openParen);
    PublicPolicy policy = new PublicPolicy(null);
    policy.ingest(rpcToken);
    for (FunctionArg arg : args) {
      storage.add(new FieldDefinition(policy, null, arg.type, arg.argNameToken, null, null, null, null, null));
    }
    genMessageType = new TyNativeMessage(TypeBehavior.ReadOnlyNativeValue, rpcToken, name.cloneWithNewText(genMessageTypeName()), storage);
    return genMessageType;
  }

  public String genMessageTypeName() {
    return "__Gen" + name.text.toUpperCase();
  }

}
