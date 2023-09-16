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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DocumentEvent;
import org.adamalang.translator.tree.definitions.config.DefineDocumentEvent;

import java.util.HashMap;

/** responsible for writing event handlers */
public class CodeGenEventHandlers {
  private static void writeBody(DefineDocumentEvent dce, StringBuilderWithTabs sb, final Environment environment) {
    sb.append("{");
    sb.tabUp().writeNewline();
    if (dce.which.isStaticPolicy && dce.contextVariable != null) {
      sb.append("CoreRequestContext ").append(dce.contextVariable).append(" = __context;").writeNewline();
    }
    dce.code.specialWriteJava(sb, environment, false, true);
    sb.append("}").writeNewline();
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

  private static class EventShred {
    private final DocumentEvent event;
    private int count;

    public EventShred(DocumentEvent event) {
      this.event = event;
      this.count = 0;
    }

    public void consider(DefineDocumentEvent dce, StringBuilderWithTabs sb, Environment environment) {
      if (event.isStaticPolicy) {
        sb.append("public static boolean ").append(event.prefix).append("__" + count).append("(StaticState __static_state, NtPrincipal __who, CoreRequestContext __context) ");
        writeBody(dce, sb, dce.nextEnvironment(environment));
      } else {
        if (event.hasPrincipal) {
          if (event.hasParameter) {
            sb.append("public ").append(event.returnType).append(" ").append(event.prefix).append("__" + count).append("(CoreRequestContext __context, NtPrincipal __who, ").append(event.parameterType).append(" ").append(dce.parameterNameToken.text).append(") ");
          } else {
            sb.append("public ").append(event.returnType).append(" ").append(event.prefix).append("__" + count).append("(CoreRequestContext __context, NtPrincipal __who) ");
          }
        } else {
          sb.append("public ").append(event.returnType).append(" ").append(event.prefix).append("__" + count).append("() ");
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
            sb.append("}").writeNewline();
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
            sb.append("public boolean ").append(event.prefix).append("(CoreRequestContext __cvalue, ").append(event.parameterType).append(" __pvalue) {").tabUp().writeNewline();
          } else {
            sb.append("public boolean ").append(event.prefix).append("(CoreRequestContext __cvalue) {").tabUp().writeNewline();
          }
          if (count > 0) {
            sb.append("boolean __result = false;").writeNewline();
            for (var k = 0; k < count; k++) {
              if (event.hasParameter) {
                sb.append("if (").append(event.prefix).append("__" + k).append("(__cvalue, __cvalue.who, __pvalue)) __result = true;").writeNewline();
              } else {
                sb.append("if (").append(event.prefix).append("__" + k).append("(__cvalue, __cvalue.who)) __result = true;").writeNewline();
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
          if (event.hasPrincipal) {
            if (event.hasParameter) {
              sb.append("public void ").append(event.prefix).append("(CoreRequestContext __cvalue, ").append(event.parameterType).append(" __pvalue) {");
            } else {
              sb.append("public void ").append(event.prefix).append("(CoreRequestContext __cvalue) {");
            }
          } else {
            sb.append("public void ").append(event.prefix).append("() {");
          }
          if (count > 0) {
            sb.tabUp().writeNewline();
            for (var k = 0; k < count; k++) {
              if (event.hasPrincipal) {
                if (event.hasParameter) {
                  sb.append(event.prefix).append("__" + k + "(__cvalue, __cvalue.who, __pvalue);");
                } else {
                  sb.append(event.prefix).append("__" + k + "(__cvalue, __cvalue.who);");
                }
              } else {
                sb.append(event.prefix).append("__" + k + "();");
              }
              if (k == count - 1) {
                sb.tabDown();
              }
              sb.writeNewline();
            }
          }
          sb.append("}").writeNewline();
        }
      }
    }
  }
}
