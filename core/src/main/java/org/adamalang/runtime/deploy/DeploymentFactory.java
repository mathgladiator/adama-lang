/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.deploy;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * converts a DeploymentPlan into a LivingDocumentFactoryFactory; if this can be created, then it is
 * in good order
 */
public class DeploymentFactory implements LivingDocumentFactoryFactory {
  public final String name;
  public final DeploymentPlan plan;
  private final HashMap<String, LivingDocumentFactory> factories;
  private final Deliverer deliverer;

  /**
   * @param spacePrefix - used for debugging by generating a relevant class name
   * @param newClassId - generates a unique number for the class name
   * @param prior - a prior deployment plan; this caches compilation between updates
   * @param plan - the plan to compile
   * @throws ErrorCodeException
   */
  public DeploymentFactory(String name, String spacePrefix, AtomicInteger newClassId, DeploymentFactory prior, DeploymentPlan plan, Deliverer deliverer) throws ErrorCodeException {
    this.name = name;
    this.deliverer = deliverer;
    this.factories = new HashMap<>();
    for (Map.Entry<String, DeployedVersion> entry : plan.versions.entrySet()) {
      LivingDocumentFactory factory = null;
      if (prior != null) {
        if (prior.plan.versions.containsKey(entry.getKey())) {
          if (prior.plan.versions.get(entry.getKey()).equals(entry.getValue())) {
            factory = prior.factories.get(entry.getKey());
          }
        }
      }
      if (factory == null) {
        factory = compile(name, spacePrefix + newClassId.getAndIncrement(), entry.getValue().main, entry.getValue().includes, deliverer);
      }
      factories.put(entry.getKey(), factory);
    }
    this.plan = plan;
  }


  public static LivingDocumentFactory compile(String spaceName, String className, final String code, HashMap<String, String> includes, Deliverer deliverer) throws ErrorCodeException {
    try {
      final var options = CompilerOptions.start().make();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      document.setClassName(className);
      document.setIncludes(includes);
      final var tokenEngine = new TokenEngine(spaceName, code.codePoints().iterator());
      final var parser = new Parser(tokenEngine);
      parser.document().accept(document);
      if (!document.check(state)) {
        throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_CANT_TYPE_LANGUAGE, document.errorsJson());
      }
      final var java = document.compileJava(state);
      JsonStreamWriter reflection = new JsonStreamWriter();
      document.writeTypeReflectionJson(reflection);
      return new LivingDocumentFactory(className, java, reflection.toString(), deliverer);
    } catch (AdamaLangException ex) {
      throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_CANT_PARSE_LANGUAGE, ex);
    }
  }

  @Override
  public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    String versionToUse = plan.pickVersion(key.key);
    callback.success(factories.get(versionToUse));
  }

  @Override
  public Collection<String> spacesAvailable() {
    return Collections.singleton(name);
  }
}
