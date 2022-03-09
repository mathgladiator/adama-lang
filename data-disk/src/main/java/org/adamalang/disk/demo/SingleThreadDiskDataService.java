/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.demo;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.disk.demo.records.Header;
import org.adamalang.disk.demo.records.BatchPatch;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.json.JsonAlgebra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Stack;

/** a very rough disk data service */
public class SingleThreadDiskDataService implements DataService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SingleThreadDiskDataService.class);
  private final File root;
  private final DiskMetrics metrics;

  public SingleThreadDiskDataService(File root, DiskMetrics metrics) {
    this.root = root;
    this.metrics = metrics;
    if (!(root.exists() && root.isDirectory()) && !root.mkdir()) {
      throw new RuntimeException("failed to create root directory:" + root.getAbsolutePath());
    }
  }

  private File fileFor(Key key) {
    return new File(new File(root, key.space), key.key + ".log");
  }
  private File fileFor2(Key key) {
    return new File(new File(root, key.space), key.key + ".log.progress");
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callbackRaw) {
    Callback<LocalDocumentChange> callback = metrics.disk_get.wrap(callbackRaw);
    File fileToUse = fileFor(key);
    try {
      AutoMorphicAccumulator<String> result = JsonAlgebra.mergeAccumulator();
      int reads = 0;
      DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(fileToUse)));
      try {
        try {
          Header header = Header.from(input);
          RemoteDocumentUpdate[] updates;
          while ((updates = BatchPatch.read(header.version, input)) != null) {
            for (RemoteDocumentUpdate update : updates) {
              reads++;
              result.next(update.redo);
            }
          }
        } finally {
          input.close();
        }
        callback.success(new LocalDocumentChange(result.finish(), reads));
      } catch (IOException ex) {
        LOGGER.error("disk-get-failure", ex);
        callback.failure(new ErrorCodeException(ErrorCodes.DISK_GET_IO_EXCEPTION, ex));
      }
    } catch (FileNotFoundException ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED, ex));
    }
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callbackRaw) {
    Callback<Void> callback = metrics.disk_initialize.wrap(callbackRaw);
    File file = fileFor(key);
    if (file.exists()) {
      callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE));
      return;
    }
    try {
      file.getParentFile().mkdirs();
      DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
      try {
        Header.writeNewHeader(output);
        BatchPatch.write(new RemoteDocumentUpdate[]{patch}, output);
        output.flush();
      } finally {
        output.close();
      }
      callback.success(null);
    } catch (IOException ex) {
      LOGGER.error("disk-initialize-failure", ex);
      callback.failure(new ErrorCodeException(ErrorCodes.DISK_INITIALIZE_IO_EXCEPTION, ex));
    }
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callbackRaw) {
    Callback<Void> callback = metrics.disk_patch.wrap(callbackRaw);
    File file = fileFor(key);
    if (!file.exists()) {
      callback.failure(new ErrorCodeException(ErrorCodes.DISK_UNABLE_TO_PATCH_FILE_NOT_FOUND));
      return;
    }
    try {
      DataOutputStream output = new DataOutputStream(new FileOutputStream(file, true));
      try {
        BatchPatch.write(patches, output);
        output.flush();
      } finally {
        output.close();
      }
      callback.success( null );
    } catch (IOException ex) {
      LOGGER.error("disk-patch-failure", ex);
      callback.failure(new ErrorCodeException(ErrorCodes.DISK_PATCH_IO_EXCEPTION, ex));
    }
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callbackRaw) {
    Callback<LocalDocumentChange> callback = metrics.disk_get.wrap(callbackRaw);
    File fileToUse = fileFor(key);
    if (!fileToUse.exists()) {
      callback.failure(new ErrorCodeException(ErrorCodes.DISK_UNABLE_TO_COMPUTE_FILE_NOT_FOUND));
      return;
    }

    if (method == ComputeMethod.Rewind) {
      Stack<String> toUndo = new Stack<>();
      int reads = 0;
      try {
        DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(fileToUse)));
        try {
          Header header = Header.from(input);
          RemoteDocumentUpdate[] updates;
          while ((updates = BatchPatch.read(header.version, input)) != null) {
            for (RemoteDocumentUpdate update : updates) {
              reads++;
              if (update.seqBegin >= seq) {
                toUndo.push(update.undo);
              }
            }
          }
        } finally {
          input.close();
        }
        AutoMorphicAccumulator<String> undo = JsonAlgebra.mergeAccumulator();
        while (!toUndo.empty()) {
          undo.next(toUndo.pop());
        }
        if (undo.empty()) {
          callback.failure(new ErrorCodeException(ErrorCodes.DISK_COMPUTE_REWIND_NOTHING_TO_DO));
          return;
        }
        callback.success(new LocalDocumentChange(undo.finish(), reads));
      } catch (IOException ex) {
        LOGGER.error("disk-compute-rewind-failure", ex);
        callback.failure(new ErrorCodeException(ErrorCodes.DISK_COMPUTE_REWIND_IOEXCEPTION, ex));
      }
    } else if (method == ComputeMethod.HeadPatch) {
      AutoMorphicAccumulator<String> result = JsonAlgebra.mergeAccumulator();
      int reads = 0;
      try {
        DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(fileToUse)));
        try {
          Header header = Header.from(input);
          RemoteDocumentUpdate[] updates;
          while ((updates = BatchPatch.read(header.version, input)) != null) {
            for (RemoteDocumentUpdate update : updates) {
              reads++;
              if (update.seqBegin > seq) {
                result.next(update.redo);
              }
            }
          }
        } finally {
          input.close();
        }
        if (result.empty()) {
          callback.failure(new ErrorCodeException(ErrorCodes.DISK_COMPUTE_HEADPATCH_NOTHING_TO_DO));
          return;
        }
        callback.success(new LocalDocumentChange(result.finish(), reads));
      } catch (IOException ex) {
        LOGGER.error("disk-compute-head-patch-failure", ex);
        callback.failure(new ErrorCodeException(ErrorCodes.DISK_COMPUTE_HEADPATCH_IOEXCEPTION, ex));
      }
    } else {
      callback.failure(new ErrorCodeException(ErrorCodes.DISK_COMPUTE_INVALID_METHOD));
    }
  }

  @Override
  public void delete(Key key, Callback<Void> callbackRaw) {
    Callback<Void> callback = metrics.disk_delete.wrap(callbackRaw);
    File file = fileFor(key);
    if (file.exists()) {
      if (file.delete()) {
        callback.success(null);
      } else {
        LOGGER.error("disk-failed-delete", file.getAbsolutePath());
        callback.failure(new ErrorCodeException(ErrorCodes.DISK_UNABLE_TO_DELETE));
      }
    } else {
      callback.success(null);
    }
  }

  @Override
  public void compactAndSnapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callbackRaw) {
    Callback<Integer> callback = metrics.disk_delete.wrap(callbackRaw);
    if (history <= 0) {
      callback.failure(new ErrorCodeException(ErrorCodes.DISK_UNABLE_TO_COMPACT_NON_POSITIVE_HISTORY));
      return;
    }
    File fileToUse = fileFor(key);
    if (!fileToUse.exists()) {
      callback.failure(new ErrorCodeException(ErrorCodes.DISK_UNABLE_TO_COMPACT_FILE_NOT_FOUND));
      return;
    }
    ArrayList<RemoteDocumentUpdate> results = new ArrayList<>();
    try {
      DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(fileToUse)));
      try {
        Header header = Header.from(input);
        RemoteDocumentUpdate[] updates;
        while ((updates = BatchPatch.read(header.version, input)) != null) {
          for (RemoteDocumentUpdate update : updates) {
            results.add(update);
          }
        }
      } finally {
        input.close();
      }
    } catch (IOException failedDuringRead) {
      LOGGER.error("disk-compact-read-failure", failedDuringRead);
      callback.failure(new ErrorCodeException(ErrorCodes.DISK_COMPACT_READ_IO_EXCEPTION, failedDuringRead));
      return;
    }

    if (results.size() > history) {
      AutoMorphicAccumulator<String> mergeRedo = JsonAlgebra.mergeAccumulator();
      AutoMorphicAccumulator<String> mergeUndo = JsonAlgebra.mergeAccumulator();
      int pivot = results.size() - history;
      long assetBytes = 0;
      for (int k = 0; k <= pivot; k++) {
        mergeRedo.next(results.get(k).redo);
        mergeUndo.next(results.get(pivot - k).undo);
        assetBytes += results.get(k).assetBytes;
      }
      RemoteDocumentUpdate[] holder = new RemoteDocumentUpdate[1];
      holder[0] = new RemoteDocumentUpdate(results.get(0).seqBegin, results.get(pivot).seqEnd, null, "{\"method\":\"compact\"}", mergeRedo.finish(), mergeUndo.finish(), results.get(pivot).requiresFutureInvalidation, results.get(pivot).whenToInvalidateMilliseconds, assetBytes, UpdateType.Internal);
      File toMove = fileFor2(key);
      try {
        DataOutputStream output = new DataOutputStream(new FileOutputStream(toMove));
        try {
          Header.writeNewHeader(output);
          BatchPatch.write(holder, output);
          for (int k = pivot + 1; k < results.size(); k++) {
            holder[0] = results.get(k);
            BatchPatch.write(holder, output);
          }
          output.flush();
        } finally {
          output.close();
        }
        Files.move(toMove.toPath(), fileToUse.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException failedDuringRebuild) {
        LOGGER.error("disk-compact-rebuild-failure", failedDuringRebuild);
        failedDuringRebuild.printStackTrace();
        callback.failure(new ErrorCodeException(ErrorCodes.DISK_COMPACT_WRITE_IO_EXCEPTION, failedDuringRebuild));
        return;
      }
      callback.success(pivot);
    } else {
      callback.success(0);
    }
  }
}
