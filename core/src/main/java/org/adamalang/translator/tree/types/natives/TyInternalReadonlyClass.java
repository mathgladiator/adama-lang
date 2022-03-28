package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class TyInternalReadonlyClass extends TyType {
  private final Class<?> clazz;

  public TyInternalReadonlyClass(Class<?> clazz) {
    super(TypeBehavior.ReadOnlyNativeValue);
    this.clazz = clazz;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    throw new UnsupportedOperationException("internal types can't be emitted");
  }

  @Override
  public String getAdamaType() {
    return "internal<" + clazz.getSimpleName() + ">";
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    return clazz.getName();
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    return clazz.getName();
  }

  @Override
  public TyType makeCopyWithNewPosition(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyInternalReadonlyClass(this.clazz).withPosition(position);
  }

  @Override
  public void typing(Environment environment) {
  }

  public TyType getLookupType(Environment environment, String field) {
    try {
      Field fType = clazz.getField(field);
      if (fType.getType() == String.class) {
        return new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("string"));
      } else if (fType.getType() == NtClient.class) {
        return new TyNativeClient(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("client"));
      } else {
        environment.document.createError(this, "Field '" + field + "' had a type we didn't recognize in internal type: " + clazz.getSimpleName(), "InternalTypes");
        return null;
      }
    } catch (Exception ex) {
      environment.document.createError(this, "Field '" + field + "' was not found in internal type: " + clazz.getSimpleName(), "InternalTypes");
      return null;
    }
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    throw new UnsupportedOperationException("internal types can't be reflected");
  }
}
