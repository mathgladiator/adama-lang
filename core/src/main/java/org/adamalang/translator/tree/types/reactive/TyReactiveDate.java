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
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.DateConstant;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TySimpleReactive;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeDate;
import org.adamalang.translator.tree.types.traits.IsOrderable;

/** Type for a reactive single date in the typical gregorian calendar */
public class TyReactiveDate extends TySimpleReactive implements //
    IsOrderable {
  public TyReactiveDate(final boolean readonly, final Token token) {
    super(readonly, token, "RxDate");
  }

  @Override
  public String getAdamaType() {
    return "r<date>";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveDate(readonly, token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_value");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    writer.writeString("date");
    writer.endObject();
  }

  @Override
  public Expression inventDefaultValueExpression(DocumentPosition forWhatExpression) {
    return new DateConstant(1, 1, 1, token);
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    return new TyNativeDate(TypeBehavior.ReadOnlyNativeValue, null, token);
  }
}
