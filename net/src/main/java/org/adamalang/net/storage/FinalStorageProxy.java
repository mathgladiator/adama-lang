package org.adamalang.net.storage;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.MachineIdentity;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.proxy.ProxyDataService;
import org.adamalang.runtime.data.*;

public class FinalStorageProxy implements DataService {
  private final String selfTarget;
  private final FinderService finder;
  private final DataService local;
  private final Client client;

  public FinalStorageProxy(String selfTarget, FinderService finder, DataService local, Client client) {
    this.selfTarget = selfTarget;
    this.finder = finder;
    this.local = local;
    this.client = client;
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    finder.find(key, new Callback<FinderService.Result>() {
      @Override
      public void success(FinderService.Result result) {
        if (result.location == FinderService.Location.Machine) {
          if (result.value.equals(selfTarget)) {
            local.get(key, callback);
          } else {
            client.getProxy(result.value, new Callback<ProxyDataService>() {
              @Override
              public void success(ProxyDataService proxy) {
                proxy.get(key, callback);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
              }
            });
          }
        } else if (result.location == FinderService.Location.Archive) {
          // TODO pull from archive
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    finder.create(key, new Callback<Void>() {
      @Override
      public void success(Void value) {
        finder.takeover(key, new Callback<Void>() {
          @Override
          public void success(Void value) {
            local.initialize(key, patch, callback);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        //
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    finder.find(key, new Callback<FinderService.Result>() {
      @Override
      public void success(FinderService.Result result) {
        if (result.location == FinderService.Location.Machine) {
          if (result.value.equals(selfTarget)) {
            local.patch(key, patches, callback);
          } else {
            client.getProxy(result.value, new Callback<ProxyDataService>() {
              @Override
              public void success(ProxyDataService proxy) {
                proxy.patch(key, patches, callback);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
              }
            });
          }
        } else {
          // can't patch non-local

        }

      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {

  }

  @Override
  public void delete(Key key, Callback<Void> callback) {

  }

  @Override
  public void compactAndSnapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {

  }
}
