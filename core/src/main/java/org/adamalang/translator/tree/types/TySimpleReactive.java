/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.traits.IsReactiveValue;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailInventDefaultValueExpression;

import java.util.function.Consumer;

public abstract class TySimpleReactive extends TyType implements DetailComputeRequiresGet, //
    IsReactiveValue, //
    DetailInventDefaultValueExpression, //
    AssignmentViaSetter //
{
  public final String reactiveTreeType;
  public final Token token;

  public TySimpleReactive(final Token token, final String reactiveTreeType) {
    super(TypeBehavior.ReadWriteWithSetGet);
    this.token = token;
    this.reactiveTreeType = reactiveTreeType;
    ingest(token);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return reactiveTreeType;
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return reactiveTreeType;
  }

  @Override
  public void typing(final Environment environment) {
  }
}
