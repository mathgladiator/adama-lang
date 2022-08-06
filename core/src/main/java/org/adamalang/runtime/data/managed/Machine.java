/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data.managed;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.runtime.data.BackupResult;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;

import java.util.ArrayList;

public class Machine {
  private final Key key;
  private final Base base;
  private State state;
  private ArrayList<Action> actions;
  private boolean closed;
  private int pendingWrites;
  private Runnable cancelArchive;
  private int writesInFlight;
  private String lastArchiveKey;
  private boolean attemptClose;

  public Machine(Key key, Base base) {
    this.key = key;
    this.base = base;
    this.state = State.Unknown;
    this.actions = null;
    this.closed = false;
    this.pendingWrites = 0;
    this.cancelArchive = null;
    this.writesInFlight = 0;
    this.lastArchiveKey = null;
    this.attemptClose = false;
  }

  private void queue(Action action) {
    if (actions == null) {
      actions = new ArrayList<>();
    }
    actions.add(action);
  }

  private void failQueueWhileInExecutor(ErrorCodeException ex) {
    if (actions != null) {
      ArrayList<Action> tokill = actions;
      actions = null;
      for (Action action : tokill) {
        action.callback.failure(ex);
      }
    }
  }

  private void executeClosed() {
    closed = true;
    base.finder.free(key, base.target, new Callback<Void>() {
      @Override
      public void success(Void value) {
        base.reportSuccess();
        base.executor.execute(new NamedRunnable("machine-archive-freed") {
          @Override
          public void execute() throws Exception {
            base.documents.remove(key);
            base.data.delete(key, Callback.DONT_CARE_VOID);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        base.executor.schedule(new NamedRunnable("machine-free-retry") {
          @Override
          public void execute() throws Exception {
            executeClosed();
          }
        }, base.reportFailureGetRetryBackoff());
      }
    });
  }

  private void archive_Success(BackupResult result) {
    base.executor.execute(new NamedRunnable("machine-archive-success") {
      @Override
      public void execute() throws Exception {
        if (lastArchiveKey != null) {
          base.data.cleanUp(key, lastArchiveKey);
        }
        lastArchiveKey = result.archiveKey;
        cancelArchive = null;
        pendingWrites -= writesInFlight;
        writesInFlight = 0;
        if (pendingWrites > 0) {
          scheduleArchiveWhileInExecutor(false);
        } else if (attemptClose) {
          executeClosed();
        }
      }
    });
  }

  private void archive_Failure(Exception ex) {
    base.executor.execute(new NamedRunnable("machine-archive-failure") {
      @Override
      public void execute() throws Exception {
        cancelArchive = null;
        scheduleArchiveWhileInExecutor(true);
      }
    });
  }

  private void archiveWhileInExecutor() {
    base.data.backup(key, new Callback<>() {
      @Override
      public void success(BackupResult result) {
        base.finder.backup(key, result, base.target, new Callback<Void>() {
          @Override
          public void success(Void value) {
            archive_Success(result);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            archive_Failure(ex);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        archive_Failure(ex);
      }
    });
  }

  private void scheduleArchiveWhileInExecutor(boolean dueToFailure) {
    if (cancelArchive == null) {
      writesInFlight = pendingWrites;
      cancelArchive = base.executor.schedule(new NamedRunnable("machine-archive") {
        @Override
        public void execute() throws Exception {
          archiveWhileInExecutor();
        }
      }, dueToFailure ? base.reportFailureGetRetryBackoff() : base.archiveTimeMilliseconds);
    }
  }

  private void find_FoundMachine(String foundMachine, boolean postRestore) {
    base.executor.execute(new NamedRunnable("machine-found-machine") {
      @Override
      public void execute() throws Exception {
        if (closed || attemptClose) {
          state = State.Unknown;
          base.documents.remove(key);
          failQueueWhileInExecutor(new ErrorCodeException(ErrorCodes.MANAGED_STORAGE_CLOSED_BEFORE_FOUND));
          return;
        }
        if (foundMachine.equals(base.target)) {
          state = State.OnMachine;
          // since we found it on the machine, we _may_ have local changes
          if (!postRestore) {
            pendingWrites++;
          }
          ArrayList<Action> toact = actions;
          actions = null;
          for (Action action : toact) {
            action.action.run();
          }
          if (pendingWrites > 0) {
            scheduleArchiveWhileInExecutor(false);
          }
        } else {
          failQueueWhileInExecutor(new ErrorCodeException(ErrorCodes.MANAGED_STORAGE_WRONG_MACHINE));
        }
      }
    });
  }

  private void restore_Failed(ErrorCodeException ex) {
    base.executor.execute(new NamedRunnable("machine-restoring-failed") {
      @Override
      public void execute() throws Exception {
        state = State.Unknown;
        failQueueWhileInExecutor(ex);
        base.documents.remove(key);
      }
    });
  }

  private void find_Restore(String archiveKey) {
    base.executor.execute(new NamedRunnable("machine-found-archive") {
      @Override
      public void execute() throws Exception {
        state = State.Restoring;
        base.data.restore(key, archiveKey, new Callback<Void>() {
          @Override
          public void success(Void value) {
            base.finder.bind(key, base.target, new Callback<Void>() {
              @Override
              public void success(Void value) {
                find_FoundMachine(base.target, true);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                restore_Failed(ex);
              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            restore_Failed(ex);
          }
        });
      }
    });
  }

  private void find() {
    state = State.Finding;
    base.finder.find(key, new Callback<>() {
      @Override
      public void success(FinderService.Result found) {
        base.executor.execute(new NamedRunnable("got-find-result") {
          @Override
          public void execute() throws Exception {
            lastArchiveKey = found.archiveKey;
            if ("".equals(lastArchiveKey)) {
              lastArchiveKey = null;
            }
            if (found.location == FinderService.Location.Machine) {
              find_FoundMachine(found.machine, false);
            } else {
              find_Restore(found.archiveKey);
            }
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        base.executor.execute(new NamedRunnable("machine-find-failure") {
          @Override
          public void execute() throws Exception {
            base.documents.remove(key);
            failQueueWhileInExecutor(ex);
          }
        });
      }
    });
  }

  public void write(Action action) {
    if (attemptClose) {
      attemptClose = false;
    }
    if (closed) {
      action.callback.failure(new ErrorCodeException(ErrorCodes.MANAGED_STORAGE_WRITE_FAILED_CLOSED));
      return;
    }
    pendingWrites++;
    switch (state) {
      case Unknown:
        find();
      case Finding:
      case Restoring:
        queue(action);
        return;
      case OnMachine:
        action.action.run();
        scheduleArchiveWhileInExecutor(false);
        return;
    }
  }

  public void read(Action action) {
    if (attemptClose) {
      attemptClose = false;
    }
    if (closed) {
      action.callback.failure(new ErrorCodeException(ErrorCodes.MANAGED_STORAGE_READ_FAILED_CLOSED));
      return;
    }
    switch (state) {
      case Unknown:
        find();
      case Finding:
      case Restoring:
        queue(action);
        return;
      case OnMachine:
        action.action.run();
        return;
    }
  }

  public void close() {
    attemptClose = true;
    if (state == State.Unknown) {
      closed = true;
      return;
    }
    if (state == State.OnMachine && pendingWrites == 0) {
      executeClosed();
    }
  }

  public void delete() {
    if (lastArchiveKey != null) {
      base.data.cleanUp(key, lastArchiveKey);
      lastArchiveKey = null;
    }
  }
}
