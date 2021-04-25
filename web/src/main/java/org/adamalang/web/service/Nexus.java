/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import org.adamalang.api.AdamaService;
import org.adamalang.api.auth.AuthenticatorService;

import java.util.Collections;
import java.util.Map;

public class Nexus {
  public final Config config;
  public final Map<String, UriHandler> handlers;
  public final UriHandler passthroughHandler;
  public final AuthenticatorService authenticatorService;
  public final AdamaService service;

  public Nexus(Config config, Map<String, UriHandler> handlers, UriHandler passthroughHandler, AuthenticatorService authenticatorService, AdamaService adamaService) {
    this.config = config;
    this.handlers = Collections.unmodifiableMap(handlers);
    this.passthroughHandler = passthroughHandler;
    this.authenticatorService = authenticatorService;
    this.service = adamaService;
  }
}
