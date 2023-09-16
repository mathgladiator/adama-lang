/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
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

  public TySimpleNative(final TypeBehavior behavior, final String javaConcreteType, final String javaBoxType) {
    super(behavior);
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
