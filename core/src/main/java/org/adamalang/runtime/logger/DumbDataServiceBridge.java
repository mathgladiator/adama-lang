package org.adamalang.runtime.logger;

import org.adamalang.runtime.contracts.DataCallback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.TransactionLogger;
import org.adamalang.runtime.exceptions.ErrorCodeException;

public class DumbDataServiceBridge implements DataService {
  private final TransactionLogger logger;

  public DumbDataServiceBridge(TransactionLogger logger) {
    this.logger = logger;
  }

  @Override
  public void create(DataCallback<Long> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void get(String gameSpace, long documentId, DataCallback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void patch(long documentId, DataService.RemoteDocumentUpdate patch, DataCallback<Void> callback) {
    try {
      logger.ingest(new Transaction(patch.seq, patch.request, patch.redo, patch.undo, new TransactionResult(patch.requiresFutureInvalidation, patch.whenToInvalidateMilliseconds, patch.seq)));
      callback.success(null);
    } catch (ErrorCodeException ece) {
      callback.failure(0, ece);
    }
  }

  @Override
  public long fork(long documentId, long seqEnd, DataCallback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void rewind(long documentId, long seqEnd, DataCallback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unsend(long documentId, long seqBegin, long seqEnd, DataCallback<LocalDocumentChange> callback) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(long documentId, DataCallback<Long> callback) {
    throw new UnsupportedOperationException();
  }
}
