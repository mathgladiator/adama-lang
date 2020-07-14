/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNativeOnlyForSet;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;
import org.adamalang.translator.tree.types.traits.details.DetailTypeProducesRootLevelCode;

public class TyNativeMessage extends TyType implements IsStructure, //
    DetailTypeProducesRootLevelCode, //
    DetailHasBridge, //
    DetailNativeDeclarationIsNotStandard, //
    AssignmentViaNativeOnlyForSet {
  public Token messageToken;
  public String name;
  public Token nameToken;
  public StructureStorage storage;

  public TyNativeMessage(final Token messageToken, final Token nameToken, final StructureStorage storage) {
    this.messageToken = messageToken;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.storage = storage;
    ingest(messageToken);
    ingest(storage);
  }

  @Override
  public void compile(final StringBuilderWithTabs sb, final Environment environment) {
    // a COPY of fields to preserver order
    final var fields = new ArrayList<FieldDefinition>();
    for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
      fields.add(e.getValue());
    }
    sb.append("private static class RTx" + name + " implements NtMessageBase {").tabUp().writeNewline();
    for (final FieldDefinition fd : fields) {
      sb.append("private ").append(fd.type.getJavaConcreteType(environment)).append(" ").append(fd.name).append(";").writeNewline();
    }
    { // CONSTRUCT FROM JSON
      var countDownUntilTab = storage.fields.size();
      if (countDownUntilTab == 0) {
        sb.append("private RTx" + name + "(ObjectNode payload) {}").writeNewline();
      } else {
        sb.append("private RTx" + name + "(ObjectNode payload) {").tabUp().writeNewline();
        for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
          sb.append("this.").append(e.getKey()).append(" = ");
          var bridge = "??";
          if (e.getValue().type instanceof DetailHasBridge) {
            bridge = ((DetailHasBridge) e.getValue().type).getBridge(environment);
          }
          sb.append(bridge).append(".readFromMessageObject(payload, \"").append(e.getKey()).append("\");");
          if (--countDownUntilTab <= 0) {
            sb.tabDown();
          }
          sb.writeNewline();
        }
        sb.append("}").writeNewline();
      }
      sb.append("@Override").writeNewline();
    }
    { // CONVERT TO OBJECT NODE
      sb.append("public ObjectNode convertToObjectNode() {").tabUp().writeNewline();
      sb.append("ObjectNode __node = Utility.createObjectNode();").writeNewline();
      for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
        final var outType = environment.rules.Resolve(e.getValue().type, true);
        if (outType != null && outType instanceof DetailHasBridge) {
          final var bridge = ((DetailHasBridge) outType).getBridge(environment);
          sb.append(bridge).append(".writeTo(\"").append(e.getKey()).append("\", ").append(e.getKey()).append(", __node);").writeNewline();
        }
      }
      // sb.append(context.writeToTObject.toString());
      sb.append("return __node;").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
    { // CLOSE UP THE MESSAGE WITH A CONSTRUCTOR FOR ANONYMOUS OBJECTS
      if (storage.fields.size() == 0) {
        sb.append("private RTx" + name + "() {}").tabDown().writeNewline();
      } else {
        sb.append("private RTx").append(name).append("(");
        var firstArg = true;
        for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
          if (!firstArg) {
            sb.append(", ");
          }
          firstArg = false;
          sb.append(e.getValue().type.getJavaConcreteType(environment)).append(" ").append(e.getKey());
        }
        sb.append(") {").tabUp().writeNewline();
        var countDownUntilTab = storage.fields.size();
        for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
          sb.append("this.").append(e.getKey()).append(" = ").append(e.getKey()).append(";");
          if (--countDownUntilTab <= 0) {
            sb.tabDown();
          }
          sb.writeNewline();
        }
        sb.append("}").tabDown().writeNewline();
      }
      sb.append("}").writeNewline();
    }
    { // CREATE THE BRIDGE
      sb.append("private static final MessageBridge<RTx").append(name).append("> __BRIDGE_").append(name).append(" = new MessageBridge<>() {").tabUp().writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("public RTx").append(name).append(" convert(ObjectNode __node) {").tabUp().writeNewline();
      sb.append("return new RTx").append(name).append("(__node);").tabDown().writeNewline();
      sb.append("}").writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("public RTx").append(name).append("[] makeArray(int __n) {").tabUp().writeNewline();
      sb.append("return new RTx").append(name).append("[__n];").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
      sb.append("};").writeNewline();
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(messageToken);
    yielder.accept(nameToken);
    storage.emit(yielder);
  }

  @Override
  public String getAdamaType() {
    return name;
  }

  @Override
  public String getBridge(final Environment environment) {
    return String.format("__BRIDGE_%s", name);
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return String.format("RTx%s", name);
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return String.format("RTx%s", name);
  }

  @Override
  public String getPatternWhenValueProvided(final Environment environment) {
    return "%s";
  }

  @Override
  public String getStringWhenValueNotProvided(final Environment environment) {
    return "new RTx" + name + "(Utility.EMPTY_NODE)";
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeMessage(messageToken, nameToken, storage).withPosition(position);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public StructureStorage storage() {
    return storage;
  }

  @Override
  public void typing(final Environment environment) {
    final var newEnv = environment.scope();
    for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
      e.getValue().typing(newEnv, storage);
      newEnv.rules.Resolve(e.getValue().type, false);
    }
  }
}
