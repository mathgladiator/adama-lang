/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineDocumentEvent;
import org.adamalang.translator.tree.definitions.DocumentEvent;

import java.util.HashMap;

/** responsible for writing event handlers */
public class CodeGenEventHandlers {
  private static void writeBody(DefineDocumentEvent dce, StringBuilderWithTabs sb, final Environment environment) {
    sb.append("{");
    sb.tabUp().writeNewline();
    sb.append("NtClient ").append(dce.clientVarToken.text).append(" = __who;").writeNewline();
    dce.code.specialWriteJava(sb, environment, false, true);
    sb.append("}").writeNewline();
  }

  private static class EventShred {
    private final DocumentEvent event;
    private int count;

    public EventShred(DocumentEvent event) {
      this.event = event;
      this.count = 0;
    }

    public void consider(DefineDocumentEvent dce, StringBuilderWithTabs sb, Environment environment) {
      String contextName = dce.contextVariable != null ? dce.contextVariable : "__context";
      if (event.isStaticPolicy) {
        sb.append("public static boolean ").append(event.prefix).append("__" + count).append("(StaticState __static_state, NtClient __who, CoreRequestContext ").append(contextName).append(") ");
        writeBody(dce, sb, dce.nextEnvironment(environment));
      } else {
        if (event.hasParameter) {
          sb.append("public ").append(event.returnType).append(" ").append(event.prefix).append("__" + count).append("(NtClient __who, ").append(event.parameterType).append(" ").append(dce.parameterNameToken.text).append(") ");
        } else {
          sb.append("public ").append(event.returnType).append(" ").append(event.prefix).append("__" + count).append("(NtClient __who) ");
        }
        writeBody(dce, sb, dce.nextEnvironment(environment));
      }
      count++;
    }

    public void finish(StringBuilderWithTabs sb) {
      if (event.isStaticPolicy) {
        sb.append("public static boolean " + event.prefix + "(CoreRequestContext __context) {").tabUp().writeNewline();
        if (count > 0) {
          sb.append("boolean __result = false;").writeNewline();
          sb.append("StaticState __static_state = new StaticState();").writeNewline();
          for (var k = 0; k < count; k++) {
            sb.append("if (").append(event.prefix).append("__" + k).append("(__static_state, __context.who, __context)) {").tabUp().writeNewline();
            sb.append("__result = true;").tabDown().writeNewline();
            sb.append("} else {").tabUp().writeNewline();
            sb.append("return false;").tabDown().writeNewline();
            sb.append("}");
          }
          sb.append("return __result;");
        } else {
          sb.append("return false;");
        }
        sb.tabDown().writeNewline();
        sb.append("}").writeNewline();
      } else {
        if ("boolean".equals(event.returnType)) {
          sb.append("@Override").writeNewline();
          if (event.hasParameter) {
            sb.append("public boolean ").append(event.prefix).append("(NtClient __cvalue, ").append(event.parameterType).append(" __pvalue) {").tabUp().writeNewline();
          } else {
            sb.append("public boolean ").append(event.prefix).append("(NtClient __cvalue) {").tabUp().writeNewline();
          }
          if (count > 0) {
            sb.append("boolean __result = false;").writeNewline();
            for (var k = 0; k < count; k++) {
              if (event.hasParameter) {
                sb.append("if (").append(event.prefix).append("__" + k).append("(__cvalue, __pvalue)) __result = true;").writeNewline();
              } else {
                sb.append("if (").append(event.prefix).append("__" + k).append("(__cvalue)) __result = true;").writeNewline();
              }
            }
            sb.append("return __result;");
          } else {
            sb.append("return false;");
          }
          sb.tabDown().writeNewline();
          sb.append("}").writeNewline();
        }
        if ("void".equals(event.returnType)) {
          sb.append("@Override").writeNewline();
          if (event.hasParameter) {
            sb.append("public void ").append(event.prefix).append("(NtClient __cvalue, ").append(event.parameterType).append(" __pvalue) {").tabUp().writeNewline();
          } else {
            sb.append("public void ").append(event.prefix).append("(NtClient __cvalue) {").tabUp().writeNewline();
          }
          if (count > 0) {
            for (var k = 0; k < count; k++) {
              if (event.hasParameter) {
                sb.append(event.prefix).append("__" + k + "(__cvalue, __pvalue);");
              } else {
                sb.append(event.prefix).append("__" + k + "(__cvalue);");
              }
              if (k == count - 1) {
                sb.tabDown();
              }
              sb.writeNewline();
            }
          } else {
            sb.tabDown().writeNewline();
          }
          sb.append("}").writeNewline();
        }
      }
    }
  }

  public static void writeEventHandlers(final StringBuilderWithTabs sb, final Environment environment) {
    HashMap<DocumentEvent, EventShred> shredder = new HashMap<>();
    for (DocumentEvent event : DocumentEvent.values()) {
      shredder.put(event, new EventShred(event));
    }
    for (final DefineDocumentEvent dce : environment.document.events) {
      shredder.get(dce.which).consider(dce, sb, environment.scopeAsPolicy());
    }
    for (DocumentEvent event : DocumentEvent.values()) {
      shredder.get(event).finish(sb);
    }
  }
}
