/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.structures.ReplicationDefinition;
import org.adamalang.translator.tree.types.traits.IsReactiveValue;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.function.Consumer;

public class TyReactiveReplicationStatus  extends TyType implements //
    IsReactiveValue, //
    DetailTypeHasMethods, //
    DetailHasDeltaType {

  public final ReplicationDefinition definition;

  public TyReactiveReplicationStatus(ReplicationDefinition definition) {
    super(TypeBehavior.ReadOnlyWithGet);
    this.definition = definition;
    ingest(definition);
  }

  @Override
  public void format(Formatter formatter) {
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
  }

  @Override
  public String getAdamaType() {
    return "replication";
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    return "RxReplicationStatus";
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    return "RxReplicationStatus";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyReactiveReplicationStatus(definition).withPosition(position);
  }

  @Override
  public void typing(Environment environment) {
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_value");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    writer.writeString("replication_status");
    writer.endObject();
  }

  @Override
  public String getDeltaType(Environment environment) {
    return "DReplicationStatus";
  }

  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    return null;
  }
}
