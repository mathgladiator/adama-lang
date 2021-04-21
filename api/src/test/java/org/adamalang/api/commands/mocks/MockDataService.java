package org.adamalang.api.commands.mocks;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.natives.NtClient;

import java.util.HashMap;

public class MockDataService implements DataService {
  public class LocalSite {
    public Object tree;

    public LocalSite() {
      this.tree = new HashMap<String, Object>();
    }

  }

  private long ids;


  public MockDataService() {
    this.ids = 0;
  }


  @Override
  public void create(Callback<Long> callback) {
    long id = ids;
    ids++;
    callback.success(id);
  }

  @Override
  public void get(long documentId, Callback<LocalDocumentChange> callback) {

  }

  @Override
  public void initialize(long documentId, RemoteDocumentUpdate patch, Callback<Void> callback) {

  }

  @Override
  public void patch(long documentId, RemoteDocumentUpdate patch, Callback<Void> callback) {

  }

  @Override
  public void fork(long oldDocumentId, long newDocumentId, NtClient who, String marker, Callback<LocalDocumentChange> callback) {

  }

  @Override
  public void rewind(long documentId, NtClient who, String marker, Callback<LocalDocumentChange> callback) {

  }

  @Override
  public void unsend(long documentId, NtClient who, String marker, Callback<LocalDocumentChange> callback) {

  }

  @Override
  public void delete(long documentId, Callback<Long> callback) {

  }
}
