/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.traits.CanBeNativeArray;
import org.adamalang.translator.tree.types.traits.details.DetailInventDefaultValueExpression;

public abstract class TySimpleNative extends TyType implements //
    CanBeNativeArray, //
    DetailInventDefaultValueExpression //
{
  private final String javaBoxType;
  private final String javaConcreteType;

  public TySimpleNative(final String javaConcreteType, final String javaBoxType) {
    this.javaConcreteType = javaConcreteType;
    this.javaBoxType = javaBoxType;
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return javaBoxType;
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return javaConcreteType;
  }

  @Override
  public void typing(final Environment environment) {
  }
}
