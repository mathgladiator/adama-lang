/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.jvm;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.LivingDocument;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

/** responsible for compiling java code into a LivingDocumentFactory */
public class LivingDocumentFactory {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(LivingDocumentFactory.class);
  public final String reflection;
  private final Constructor<?> constructor;
  private final Method creationPolicyMethod;
  private final Method inventionPolicyMethod;
  private final Method canSendWhileDisconnectPolicyMethod;
  public final int maximum_history;
  public final boolean delete_on_close;
  public final ServiceRegistry registry;
  public final Deliverer deliverer;
  public final long memoryUsage;

  public LivingDocumentFactory(final String spaceName, final String className, final String javaSource, String reflection, Deliverer deliverer) throws ErrorCodeException {
    final var compiler = ToolProvider.getSystemJavaCompiler();
    final var diagnostics = new DiagnosticCollector<JavaFileObject>();
    final var fileManager = new ByteArrayJavaFileManager(compiler.getStandardFileManager(null, null, null));
    final var task = compiler.getTask(null, fileManager, diagnostics, null, null, ByteArrayJavaFileManager.turnIntoCompUnits(className + ".java", javaSource));
    if (task.call() == false) {
      StringBuilder report = new StringBuilder();
      for (final Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
        report.append(diagnostic.toString() + "\n");
      }
      throw new ErrorCodeException(ErrorCodes.FACTORY_CANT_COMPILE_JAVA_CODE, report.toString());
    }
    try {
      this.deliverer = deliverer;
      final var classBytes = fileManager.getClasses();
      fileManager.close();
      long _memory = 0;
      for (byte[] bytes : classBytes.values()) {
        _memory += bytes.length;
      }
      this.memoryUsage = _memory + 65536;
      final var loader = new ByteArrayClassLoader(classBytes);
      final Class<?> clazz = Class.forName(className, true, loader);
      constructor = clazz.getConstructor(DocumentMonitor.class);
      creationPolicyMethod = clazz.getMethod("__onCanCreate", CoreRequestContext.class);
      inventionPolicyMethod = clazz.getMethod("__onCanInvent", CoreRequestContext.class);
      canSendWhileDisconnectPolicyMethod = clazz.getMethod("__onCanSendWhileDisconnected", CoreRequestContext.class);
      HashMap<String, Object> config = (HashMap<String, Object>) (clazz.getMethod("__config").invoke(null));
      maximum_history = extractMaximumHistory(config);
      delete_on_close = extractDeleteOnClose(config);
      this.reflection = reflection;
      this.registry = new ServiceRegistry();
      this.registry.resolve(spaceName, (HashMap<String, HashMap<String, Object>>) (clazz.getMethod("__services").invoke(null)));
    } catch (final Exception ex) {
      throw new ErrorCodeException(ErrorCodes.FACTORY_CANT_BIND_JAVA_CODE, ex);
    }
  }

  public boolean canInvent(CoreRequestContext context) throws ErrorCodeException {
    try {
      return (Boolean) inventionPolicyMethod.invoke(null, context);
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.FACTORY_CANT_INVOKE_CAN_INVENT, ex, LOGGER);
    }
  }

  public boolean canCreate(CoreRequestContext context) throws ErrorCodeException {
    try {
      return (Boolean) creationPolicyMethod.invoke(null, context);
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.FACTORY_CANT_INVOKE_CAN_CREATE, ex, LOGGER);
    }
  }

  public boolean canSendWhileDisconnected(CoreRequestContext context) throws ErrorCodeException {
    try {
      return (Boolean) canSendWhileDisconnectPolicyMethod.invoke(null, context);
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.FACTORY_CANT_INVOKE_CAN_SEND_WHILE_DISCONNECTED, ex, LOGGER);
    }
  }

  private static int extractMaximumHistory(HashMap<String, Object> config) {
    Object value = config.get("maximum_history");
    if (value != null && value instanceof Integer) {
      return ((Integer) value).intValue();
    } else {
      return 10000;
    }
  }

  private static boolean extractDeleteOnClose(HashMap<String, Object> config) {
    Object value = config.get("delete_on_close");
    if (value != null && value instanceof Boolean) {
      return ((Boolean) value).booleanValue();
    } else {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  public void populateTestReport(final TestReportBuilder report, final DocumentMonitor monitor, final String entropy) throws Exception {
    var candidate = prepareTestCandidate(monitor, entropy);
    final var tests = candidate.__getTests();
    for (final String test : tests) {
      report.annotate(test, (HashMap<String, Object>) new JsonStreamReader(candidate.__run_test(report, test)).readJavaTree());
      candidate = prepareTestCandidate(monitor, entropy);
    }
  }

  private LivingDocument prepareTestCandidate(final DocumentMonitor monitor, final String entropy) throws Exception {
    final var candidate = create(monitor);
    candidate.__lateBind("space", "key", Deliverer.FAILURE, ServiceRegistry.NOT_READY);
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("command");
    writer.writeString("construct");
    writer.writeObjectFieldIntro("timestamp");
    writer.writeString("0");
    writer.writeObjectFieldIntro("entropy");
    writer.writeString(entropy);
    writer.writeObjectFieldIntro("key");
    writer.writeString("key");
    writer.writeObjectFieldIntro("origin");
    writer.writeString("origin");
    writer.writeObjectFieldIntro("ip");
    writer.writeString("1.2.3.4");
    writer.writeObjectFieldIntro("who");
    writer.writeNtPrincipal(NtPrincipal.NO_ONE);
    writer.writeObjectFieldIntro("arg");
    writer.beginObject();
    writer.endObject();
    writer.endObject();
    candidate.__transact(writer.toString(), this);
    return candidate;
  }

  public LivingDocument create(final DocumentMonitor monitor) throws ErrorCodeException {
    try {
      return (LivingDocument) constructor.newInstance(monitor);
    } catch (final Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.FACTORY_CANT_CREATE_OBJECT_DUE_TO_CATASTROPHE, ex, LOGGER);
    }
  }
}
