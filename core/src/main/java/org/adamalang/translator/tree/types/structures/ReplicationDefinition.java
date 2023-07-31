/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.topo.TypeChecker;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.LinkedHashSet;
import java.util.function.Consumer;

public class ReplicationDefinition extends StructureComponent  {
  private final Token replication;
  private final Token open;
  private final Token service;
  private final Token split;
  private final Token method;
  private final Token close;
  public final Token name;
  private final Token equals;
  public final Expression expression;
  private final Token end;
  public final LinkedHashSet<String> servicesToWatch;
  public final LinkedHashSet<String> variablesToWatch;

  public ReplicationDefinition(Token replication, Token open, Token service, Token split, Token method, Token close, Token name, Token equals, Expression expression, Token end) {
    this.replication = replication;
    this.open = open;
    this.service = service;
    this.split = split;
    this.method = method;
    this.close = close;
    this.name = name;
    this.equals = equals;
    this.expression = expression;
    this.end = end;
    ingest(open);
    ingest(end);
    servicesToWatch = new LinkedHashSet<>();
    variablesToWatch = new LinkedHashSet<>();
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(replication);
    yielder.accept(open);
    yielder.accept(service);
    yielder.accept(split);
    yielder.accept(method);
    yielder.accept(close);
    yielder.accept(name);
    yielder.accept(equals);
    expression.emit(yielder);
    yielder.accept(end);
  }

  public void typing(final Environment environment) {
    // TODO
  }
}
