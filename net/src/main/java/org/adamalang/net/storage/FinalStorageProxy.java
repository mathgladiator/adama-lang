/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.storage;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
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
          // PULL FROM ARCHIVE, SLAM INTO DISK, THEN TRY TO TAKE OVER
          callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED));
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
    finder.create(key, new Callback<>() {
      @Override
      public void success(Void value) {
        finder.takeover(key, new Callback<>() {
          @Override
          public void success(Void value) {
            local.initialize(key, patch, callback);
            // Note: If this fails, then the finder is stuck
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        goTo(key, new Callback<>() {
          @Override
          public void success(DataService service) {
            service.initialize(key, patch, callback);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
      }
    });
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    goTo(key, new Callback<DataService>() {
      @Override
      public void success(DataService service) {
        service.patch(key, patches, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    goTo(key, new Callback<>() {
      @Override
      public void success(DataService service) {
        service.compute(key, method, seq, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    goTo(key, new Callback<>() {
      @Override
      public void success(DataService service) {
        service.delete(key, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void snapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    goTo(key, new Callback<>() {
      @Override
      public void success(DataService service) {
        service.snapshot(key, seq, snapshot, history, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  private void goTo(Key key, Callback<DataService> callback) {
    finder.find(key, new Callback<>() {
      @Override
      public void success(FinderService.Result result) {
        if (result.location == FinderService.Location.Machine) {
          if (result.value.equals(selfTarget)) {
            callback.success(local);
          } else {
            client.getProxy(result.value, new Callback<ProxyDataService>() {
              @Override
              public void success(ProxyDataService proxy) {
                callback.success(proxy);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
              }
            });
          }
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.STORAGE_TIER_FAILED_TO_APPLY_GOTO));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void close(Key key, Callback<Void> callback) {

  }

  @Override
  public void archive(Key key, ArchiveWriter writer) {

  }
}
