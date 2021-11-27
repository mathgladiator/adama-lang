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

import org.adamalang.runtime.contracts.ActiveKeyStream;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;

import java.util.HashMap;

public class MockDataService implements DataService {
  public class LocalSite {
    public Object tree;
    public int seq;

    public LocalSite() {
      this.tree = new HashMap<String, Object>();
      this.seq = seq;
    }

    public void ingest(String redo) {
      tree = JsonAlgebra.merge(tree, new JsonStreamReader(redo).readJavaTree());
    }
  }

  private long ids;
  private HashMap<Key, LocalSite> sites;

  public MockDataService() {
    this.ids = 0;
    this.sites = new HashMap<>();
  }

  public void create(Key key, Callback<Long> callback) {
    long id = ids;
    ids++;
    if (id >= 100) {
      callback.failure(new ErrorCodeException(12345));
      return;
    }
    callback.success(id);
  }

  @Override
  public void scan(ActiveKeyStream stream) {
    stream.finish();
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    LocalSite site = new LocalSite();
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.writeTree(site.tree);
    callback.success(new LocalDocumentChange(writer.toString()));
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    LocalSite site = new LocalSite();
    site.ingest(patch.redo);
    site.seq = patch.seq;
    sites.put(key, site);
    callback.success(null);
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    LocalSite site = new LocalSite();
    site.ingest(patch.redo);
    site.seq = patch.seq;
    sites.put(key, site);
    callback.success(null);
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {

  }

  @Override
  public void delete(Key key, Callback<Void> callback) {

  }
}
