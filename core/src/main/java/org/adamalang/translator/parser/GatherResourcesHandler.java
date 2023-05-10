/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.parser;

import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.definitions.*;
import org.adamalang.translator.tree.definitions.config.DefineDocumentEvent;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsStructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;

/** helper class to gather the includes and resolve them recursively */
public class GatherResourcesHandler implements TopLevelDocumentHandler {
  public final Function<String, String> resolver;
  public final HashSet<String> includes;
  public final ArrayList<String> errors;

  public GatherResourcesHandler(Function<String, String> resolver) {
    this.resolver = resolver;
    this.includes = new HashSet<>();
    this.errors = new ArrayList<>();
  }

  @Override
  public void add(BubbleDefinition bd) {

  }

  @Override
  public void add(DefineConstructor dc) {

  }

  @Override
  public void add(DefineCustomPolicy customPolicy) {

  }

  @Override
  public void add(DefineDispatcher dd) {

  }

  @Override
  public void add(DefineDocumentEvent dce) {

  }

  @Override
  public void add(DefineFunction func) {

  }

  @Override
  public void add(DefineHandler handler) {

  }

  @Override
  public void add(DefineStateTransition transition) {

  }

  @Override
  public void add(DefineTest test) {

  }

  @Override
  public void add(FieldDefinition fd) {

  }

  @Override
  public void add(IsEnum storage) {

  }

  @Override
  public void add(IsStructure storage) {

  }

  @Override
  public void add(Token token) {

  }

  @Override
  public void add(AugmentViewerState avs) {

  }

  @Override
  public void add(DefineRPC rpc) {

  }

  @Override
  public void add(DefineStatic ds) {

  }

  @Override
  public void add(DefineWebGet dwg) {

  }

  @Override
  public void add(DefineWebPut dwp) {

  }

  @Override
  public void add(DefineWebOptions dwo) {

  }

  @Override
  public void add(DefineWebDelete dwd) {

  }

  @Override
  public void add(Include in) {
    includes.add(in.resource.text);
    String code = resolver.apply(in.resource.text);
    if (code != null) {
      final var tokenEngine = new TokenEngine(in.resource.text, code.codePoints().iterator());
      final var parser = new Parser(tokenEngine);
      try {
        parser.document().accept(this);
      } catch (AdamaLangException ale) {
        errors.add(in.resource.text + ":" + ale.getMessage());
      }
    } else {
      errors.add("could not resolve: " + in.resource.text);
    }
  }

  @Override
  public void add(DefineService ds) {
  }

  @Override
  public void add(DefineAuthorization da) {}

  @Override
  public void add(DefinePassword dp) {}
}
