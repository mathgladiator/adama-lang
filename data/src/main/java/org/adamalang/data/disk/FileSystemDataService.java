/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.data.disk;

import org.adamalang.data.ErrorCodes;
import org.adamalang.runtime.contracts.DataCallback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;

/** a very simple implementation of the data service */
public class FileSystemDataService implements DataService {

  public final File root;
  private final Random rng;

  class Record {
    public final String forwardDelta;
    public final String reverseDelta;
    public final String metadata;
    public final String request;

    public Record(final BufferedReader buffered) throws IOException {
      request = buffered.readLine();
      if (request == null) {
        forwardDelta = null;
        reverseDelta = null;
        metadata = null;
      } else {
        forwardDelta = buffered.readLine();
        if (forwardDelta == null) {
          throw new IOException("incomplete record, missing forward delta");
        }
        reverseDelta = buffered.readLine();
        if (reverseDelta == null) {
          throw new IOException("incomplete record, missing reverse delta");
        }
        metadata = buffered.readLine();
        if (metadata == null) {
          throw new IOException("incomplete record, missing metadata");
        }
      }
    }

    public boolean valid() {
      return request != null;
    }
  }

  public FileSystemDataService(final File root) {
    this.root = root;
    this.rng = new Random();
  }

  @Override
  public synchronized void create(DataCallback<Long> callback) {
    try {
      for (int attempt = 0; attempt < 10; attempt++) {
        long documentId = Math.abs(rng.nextLong() + System.nanoTime());
        File file = new File(root, documentId + ".jsonlog");
        if (file.createNewFile()) {
          callback.success(documentId);
          return;
        }
      }
      callback.failure(new ErrorCodeException(ErrorCodes.E4_FS_DATASERVICE_UNABLE_CREATE_LOG, new RuntimeException("failed to create new jsonlog file")));
    } catch (Throwable ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.E4_FS_DATASERVICE_CRASHED_CREATE_LOG, ex));
    }
  }

  @Override
  public synchronized void get(long documentId, DataCallback<LocalDocumentChange> callback) {
    try {
      File file = new File(root, documentId + ".jsonlog");
      if (file.exists()) {
        final var buffered = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        try {
          Object state = new HashMap<String, Object>();
          Record record;
          while ((record = new Record(buffered)).valid()) {
            state = JsonAlgebra.merge(state, new JsonStreamReader(record.forwardDelta).readJavaTree());
          }
          JsonStreamWriter writer = new JsonStreamWriter();
          writer.writeTree(state);
          callback.success(new LocalDocumentChange(writer.toString(), 0));
        } finally {
          buffered.close();
        }
      } else {
        callback.failure(new ErrorCodeException(ErrorCodes.E4_FS_DATASERVICE_FILE_NOT_FOUND, new RuntimeException("file not found:" + file.toString())));
      }
    } catch (final Throwable ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.E4_FS_DATASERVICE_CRASHED_GET_LOG, ex));
    }
  }

  private void append(File file, RemoteDocumentUpdate patch, DataCallback<Void> callback) throws Exception {
    PrintWriter writer = new PrintWriter(new FileOutputStream(file, true), false, StandardCharsets.UTF_8);
    writer.println(patch.request);
    writer.println(patch.redo);
    writer.println(patch.undo);
    JsonStreamWriter meta = new JsonStreamWriter();
    meta.beginObject();
    meta.writeObjectFieldIntro("needsInvalidation");
    meta.writeBoolean(patch.requiresFutureInvalidation);
    meta.writeObjectFieldIntro("whenToInvalidMilliseconds");
    meta.writeInteger(patch.whenToInvalidateMilliseconds);
    meta.writeObjectFieldIntro("seq");
    meta.writeInteger(patch.seq);
    meta.endObject();;
    writer.println(meta.toString());
    writer.flush();
    writer.close();
    callback.success(null);
  }

  @Override
  public void initialize(long documentId, RemoteDocumentUpdate patch, DataCallback<Void> callback) {
    try {
      File file = new File(root, documentId + ".jsonlog");
      if (file.length() == 0) {
        append(file, patch, callback);
      } else {
        callback.failure(new ErrorCodeException(ErrorCodes.E4_FS_DATASERVICE_FILE_EXISTS_FOR_INITIALIZE, new RuntimeException(documentId + " was not empty")));
      }
    } catch (final Throwable ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.E4_FS_DATASERVICE_CRASHED_INITIALIZE, ex));
    }
  }

  @Override
  public synchronized void patch(long documentId, RemoteDocumentUpdate patch, DataCallback<Void> callback) {
    try {
      File file = new File(root, documentId + ".jsonlog");
      append(file, patch, callback);
    } catch (final Throwable ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.E4_FS_DATASERVICE_CRASHED_PATCH, ex));
    }
  }

  @Override
  public long fork(long oldDocumentId, long newDocumentId, long seqEnd, DataCallback<LocalDocumentChange> callback) {
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
