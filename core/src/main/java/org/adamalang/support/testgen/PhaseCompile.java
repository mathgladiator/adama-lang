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
package org.adamalang.support.testgen;

import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class PhaseCompile {
  public static LivingDocumentFactory go(final String className, final String java, final StringBuilder outputFile) throws Exception {
    final var memoryResultsCompiler = new ByteArrayOutputStream();
    final var ps = new PrintStream(memoryResultsCompiler);
    System.setErr(ps);
    outputFile.append("--=[LivingDocumentFactory COMPILING]=---").append("\n");
    LivingDocumentFactory factory = new LivingDocumentFactory("test", className, java, "{}", Deliverer.FAILURE, new TreeMap<>());
    if (factory != null) {
      outputFile.append("--=[LivingDocumentFactory MADE]=---\n");
    }
    return factory;
  }
}
