package org.adamalang.common.codec;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;
import java.util.TreeMap;

/** exceptionally simply code generator for building codecs against a Netty ByteBuf. The ideal is to minimize as much overhead as possible and get bytes in and out using Netty's data structures */
public class CodecCodeGen {

  private static int[] versions(Class<?> clazz) {
    TypeId typeId = clazz.getAnnotation(TypeId.class);
    if (typeId == null) {
      throw new RuntimeException(clazz.toString() + " has no @TypeId");
    };
    int currentTypeId = typeId.value();
    PriorTypeId priorTypeId = clazz.getAnnotation(PriorTypeId.class);
    if (priorTypeId != null) {
      return new int[] { currentTypeId, priorTypeId.value() };
    } else {
      return new int[] { currentTypeId };
    }
  }

  public static Field[] getFields(Class<?> clazz, boolean isNew) {
    TreeMap<Integer, Field> fields = new TreeMap<>();
    for (Field field : clazz.getFields()) {
      FieldOrder order = field.getAnnotation(FieldOrder.class);
      if (order == null) {
        throw new RuntimeException(clazz.getSimpleName() + " has field '" + field.getName() + "' which has no order");
      }
      boolean onlyNew = field.getAnnotation(FieldNew.class) != null;
      boolean onlyOld = field.getAnnotation(FieldOld.class) != null;
      boolean keep = isNew ? (!onlyOld) : (!onlyNew);
      if (keep) {
        if (!fields.containsKey(order.value())) {
          fields.put(order.value(), field);
        } else {
          throw new RuntimeException(clazz.getSimpleName() + " has two or more fields with order '" + order.value() + "'");
        }
      }
    }
    Field[] arr = new Field[fields.size()];
    int at = 0;
    for (Field field : fields.values()) {
      arr[at] = field;
      at++;
    }
    return arr;
  }

  public static String readerOf(Field field) {
    if (field.getType() == int.class || field.getType() == Integer.class) {
      return "buf.readIntLE()";
    }
    if (field.getType() == short.class || field.getType() == Short.class) {
      return "buf.readShortLE()";
    }
    if (field.getType() == double.class || field.getType() == Double.class) {
      return "buf.readDoubleLE()";
    }
    if (field.getType() == String.class) {
      return "Helper.readString(buf)";
    }
    if (field.getType().getAnnotation(TypeId.class) != null) {
      return "read_" + field.getType().getSimpleName() + "(buf)";
    }
    throw new RuntimeException(field.getName() + " has a type we don't know about.. yet");
  }

  public static String write(Field field, String value) {
    if (field.getType() == int.class || field.getType() == Integer.class) {
      return "buf.writeIntLE("+value+")";
    }
    if (field.getType() == short.class || field.getType() == Short.class) {
      return "buf.writeShortLE("+value+")";
    }
    if (field.getType() == double.class || field.getType() == Double.class) {
      return "buf.writeDoubleLE("+value+")";
    }
    if (field.getType() == String.class) {
      return "Helper.writeString(buf, "+value+");";
    }
    if (field.getType().getAnnotation(TypeId.class) != null) {
      return "write(buf, " + value + ");";
    }
    throw new RuntimeException(field.getName() + " has a type we don't know about.. yet");
  }

  public static String assembleCodec(String packageName, String className, Class<?>... classes) {
    StringBuilder sb = new StringBuilder();
    sb.append("package " + packageName + ";\n\n");

    sb.append("import io.netty.buffer.ByteBuf;\n\n");
    sb.append("import org.adamalang.common.codec.Helper;\n\n");
    for (Class<?> clazz : classes) {
      sb.append("import ").append(clazz.getName().replace('$', '.')).append(";\n");
    }
    sb.append("\n");
    sb.append("public class ").append(className).append(" {\n");
    sb.append("  public static interface Handler {\n");
    for (Class<?> clazz : classes) {
      sb.append("    public void handle(").append(clazz.getSimpleName()).append(" payload);\n");
    }
    sb.append("  }\n\n");
    sb.append("  public static void route(ByteBuf buf, Handler handler) {\n");
    sb.append("    switch (buf.readIntLE()) {\n");
    for (Class<?> clazz : classes) {
      int[] caseIds = versions(clazz);
      for (int caseId : caseIds) {
        sb.append("      case ").append(caseId).append(":\n");

        sb.append("        handler.handle(readBody_").append(caseId).append("(buf, new ").append(clazz.getSimpleName()).append("()));\n");
        sb.append("        return;\n");
      }
    }
    sb.append("    }\n");
    sb.append("  }\n");

    for (Class<?> clazz : classes) {
      int[] caseIds = versions(clazz);
      sb.append("\n");
      sb.append("  public static ").append(clazz.getSimpleName()).append(" read_").append(clazz.getSimpleName()).append("(ByteBuf buf) {\n");
      sb.append("    switch (buf.readIntLE()) {\n");
      for (int caseId : caseIds) {
        sb.append("      case ").append(caseId).append(":\n");
        sb.append("        return readBody_").append(caseId).append("(buf, new ").append(clazz.getSimpleName()).append("());\n");
      }
      sb.append("    }\n");
      sb.append("    return null;\n");
      sb.append("  }\n");
      sb.append("\n");
      sb.append("  public static ").append(clazz.getSimpleName()).append(" readRegister_").append(clazz.getSimpleName()).append("(ByteBuf buf, ").append(clazz.getSimpleName()).append(" o) {\n");
      sb.append("    switch (buf.readIntLE()) {\n");
      for (int caseId : caseIds) {
        sb.append("      case ").append(caseId).append(":\n");
        sb.append("        return readBody_").append(caseId).append("(buf, o);\n");
      }
      sb.append("    }\n");
      sb.append("    return null;\n");
      sb.append("  }\n");
      for (int caseId : caseIds) {
        boolean primary = caseId == caseIds[0];
        sb.append("\n");
        sb.append("  private static " + clazz.getSimpleName() + " readBody_"+caseId+"(ByteBuf buf, ").append(clazz.getSimpleName()).append(" o) {\n");
        for (Field field : getFields(clazz, primary)) {
          sb.append("    o.").append(field.getName()).append(" = ").append(readerOf(field)).append(";\n");
        }
        sb.append("    return o;\n");
        sb.append("  }\n");
      }
    }

    for (Class<?> clazz : classes) {
      sb.append("\n");
      sb.append("  public static void write(ByteBuf buf, " + clazz.getSimpleName() + " o) {\n");
      sb.append("    if (o == null) {\n");
      sb.append("      buf.writeIntLE(0);\n");
      sb.append("      return;\n");
      sb.append("    }\n");
      int caseId = versions(clazz)[0];
      sb.append("    buf.writeIntLE(").append(caseId).append(");\n");
      for (Field field : getFields(clazz, true)) {
        sb.append("    ").append(write(field, "o." + field.getName())).append(";\n");
      }
      sb.append("  }\n");
    }
    sb.append("}\n");
    return sb.toString();
  }
}
