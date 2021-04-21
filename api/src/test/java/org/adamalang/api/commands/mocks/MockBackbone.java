/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.commands.mocks;

import org.adamalang.api.commands.contracts.*;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

import java.util.HashMap;

public class MockBackbone implements Backbone, TimeSource {
  public long now;
  public final DataService dataService;
  public final HashMap<String, DurableLivingDocument> documents;
  private final LivingDocumentFactory factory;

  public MockBackbone() throws Exception {
    this.now = 10000;
    this.dataService = new MockDataService();
    this.documents = new HashMap<>();
    this.factory = MockBackbone.compile("public int x; @connected(who) { return true; }");
  }

  @Override
  public void findDataService(String space, CommandRequiresDataService cmd, CommandResponder responder) {
    if (space.equals("bad")) {
      responder.error(new ErrorCodeException(42));
    } else {
      cmd.onDataServiceFound(dataService);
    }
  }

  @Override
  public void findDocument(String space, long key, CommandRequiresDocument cmd, CommandResponder responder) {
    String sKey = space + "/" + key;
    if (space.equals("bad")) {
      responder.error(new ErrorCodeException(100));
    } else {
      DurableLivingDocument doc = documents.get(sKey);
      if (doc != null) {
        cmd.onDurableDocumentFound(doc);
      } else {
        findLivingDocumentFactory(space, key, new CommandRequiresLivingDocumentFactory() {
          @Override
          public void onLivingDocumentFactory(LivingDocumentFactory factory) {
            findDataService(space, new CommandRequiresDataService() {
              @Override
              public void onDataServiceFound(DataService service) {
                DurableLivingDocument.load(key, factory, null, MockBackbone.this, service, new Callback<DurableLivingDocument>() {
                  @Override
                  public void success(DurableLivingDocument doc) {
                    cmd.onDurableDocumentFound(doc);
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    responder.error(ex);
                  }
                });
              }
            }, responder);
          }
        }, responder);
      }
    }
  }

  @Override
  public void findLivingDocumentFactory(String space, long key, CommandRequiresLivingDocumentFactory cmd, CommandResponder responder) {
    if (space.equals("bad")) {
      responder.error(new ErrorCodeException(42));
    } else {
      cmd.onLivingDocumentFactory(factory);
    }
  }

  @Override
  public void makeDocument(String space, long key, NtClient who, String arg, String entropy, DataService service, LivingDocumentFactory factory, CommandCreatesDocument cmd, CommandResponder responder) {
    try {
      DurableLivingDocument.fresh(key, factory, who, arg, entropy, null, this, service, new Callback<>() {
        @Override
        public void success(DurableLivingDocument doc) {
          doc.invalidate(new Callback<Integer>() {
            @Override
            public void success(Integer seq) {
              documents.put(space + "/" + key, doc);
              cmd.onDurableDocumentCreated(doc, seq);
            }
            @Override
            public void failure(ErrorCodeException ex) {
              responder.error(ex);
            }
          });
        }
        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  @Override
  public void invalidateAndSchedule(DurableLivingDocument document) {
    document.invalidate(new Callback<Integer>() {
      @Override
      public void success(Integer value) {
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
  }

  @Override
  public long nowMilliseconds() {
    return now;
  }

  private static HashMap<String, LivingDocumentFactory> COMPILER_CACHE = new HashMap<>();

  public static LivingDocumentFactory compile(final String code) throws Exception {
    final var options = CompilerOptions.start().enableCodeCoverage().noCost().make();
    final var globals = GlobalObjectPool.createPoolWithStdLib();
    final var state = new EnvironmentState(globals, options);
    final var document = new Document();
    document.setClassName("MeCode");
    final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
    final var parser = new Parser(tokenEngine);
    parser.document().accept(document);
    if (!document.check(state)) {
      throw new Exception("Failed to check:" + document.errorsJson());
    }
    final var java = document.compileJava(state);
    var cached = COMPILER_CACHE.get(java);
    if (cached == null) {
      cached = new LivingDocumentFactory("MeCode", java, "{}");
      COMPILER_CACHE.put(java, cached);
    }
    return cached;
  }
}
