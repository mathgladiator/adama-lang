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
package org.adamalang.translator.jvm;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.deploy.CachedByteCode;
import org.adamalang.runtime.deploy.SyncCompiler;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.ops.TestMockUniverse;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.LivingDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.TreeMap;

/** responsible for compiling java code into a LivingDocumentFactory */
public class LivingDocumentFactory {
  private static final Logger LOG = LoggerFactory.getLogger(LivingDocumentFactory.class);
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(LOG);
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
  public final boolean appMode;
  public final int appDelay;
  public final int temporalResolutionMilliseconds;

  public LivingDocumentFactory(CachedByteCode code, Deliverer deliverer, TreeMap<Integer, PrivateKeyBundle> keys) throws ErrorCodeException {
    try {
      this.deliverer = deliverer;
      long _memory = 0;
      for (byte[] bytes : code.classBytes.values()) {
        _memory += bytes.length;
      }
      this.memoryUsage = _memory + 65536;
      // NOTE: we copy because the loader will destroy aspects of the hashmap, #wild
      final var loader = new ByteArrayClassLoader(new HashMap<>(code.classBytes));
      final Class<?> clazz = Class.forName(code.className, true, loader);
      constructor = clazz.getConstructor(DocumentMonitor.class);
      creationPolicyMethod = clazz.getMethod("__onCanCreate", CoreRequestContext.class);
      inventionPolicyMethod = clazz.getMethod("__onCanInvent", CoreRequestContext.class);
      canSendWhileDisconnectPolicyMethod = clazz.getMethod("__onCanSendWhileDisconnected", CoreRequestContext.class);
      HashMap<String, Object> config = (HashMap<String, Object>) (clazz.getMethod("__config").invoke(null));
      maximum_history = extractMaximumHistory(config);
      delete_on_close = extractDeleteOnClose(config);
      int freq = extractFrequency(config);
      appMode = freq > 0;
      appDelay = freq;
      this.reflection = code.reflection;
      this.registry = new ServiceRegistry();
      this.registry.resolve(code.spaceName, (HashMap<String, HashMap<String, Object>>) (clazz.getMethod("__services").invoke(null)), keys);
      this.temporalResolutionMilliseconds = extractTemporalResolution(config);
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
      return 1000;
    }
  }

  private static int extractTemporalResolution(HashMap<String, Object> config) {
    Object value = config.get("temporal_resolution_ms");
    if (value != null && value instanceof Integer) {
      return ((Integer) value).intValue();
    } else {
      return 0; // preserves existing behavior
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

  private static int extractFrequency(HashMap<String, Object> config) {
    Object value = config.get("frequency");
    if (value != null && value instanceof Integer) {
      return ((Integer) value).intValue();
    } else {
      return 0;
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
    TestMockUniverse tmu = new TestMockUniverse(candidate);
    candidate.__lateBind("space", "key", tmu, tmu);
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
