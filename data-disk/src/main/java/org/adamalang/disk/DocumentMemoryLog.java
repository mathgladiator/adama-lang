/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.disk.files.SnapshotFileStreamEvents;
import org.adamalang.disk.files.SnapshotHeader;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.json.JsonAlgebra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public class DocumentMemoryLog {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentMemoryLog.class);

  private static class Undo {
    public final String undo;
    public final int seq;

    private Undo(String undo, int seq) {
      this.undo = undo;
      this.seq = seq;
    }
  }

  private static class Redo {
    public final String redo;
    public final int seq;

    private Redo(String redo, int seq) {
      this.redo = redo;
      this.seq = seq;
    }
  }

  // coordinates for the file on disk
  public final File spacePath;
  public final Key key;

  // is the file loaded, alive, in need of a reset
  private boolean loaded;
  private boolean reset;

  // document state
  private String document;
  private int seq;
  private int history;
  private long assetBytes;
  private boolean active;
  private ArrayList<Redo> redoLog;
  private ArrayDeque<Undo> undoStack;
  private ArrayList<Undo> undoHistory;
  private int refs;
  private final ArrayList<PostFlushCleanupEvent> cleanup;
  private long lastActivity;
  private boolean hasActivityToFlush;

  public File suffixFile(String suffix) {
    // TODO: make a better version of this
    // SEE saas/src/main/java/org/adamalang/validators/ValidateKey.java
    String fixed_filename = key.key.replaceAll(Pattern.quote("_"), "__") //
        .replaceAll(Pattern.quote("/"), "_F") //
        .replaceAll(Pattern.quote("+"), "_A") //
        .replaceAll(Pattern.quote("#"), "_P") //
        .replaceAll(Pattern.quote("="), "_E") //
        .replaceAll(Pattern.quote("\\"), "_B");
    return new File(spacePath, fixed_filename + "." + suffix);
  }

  public DocumentMemoryLog(Key key, File spacePath) {
    this.spacePath = spacePath;
    this.key = key;
    this.loaded = false;
    this.reset = false;
    this.document = null;
    this.seq = -1;
    this.history = 10000;
    this.assetBytes = 0L;
    this.active = true;
    this.redoLog = new ArrayList<>();
    this.undoStack = new ArrayDeque<>();
    this.undoHistory = new ArrayList<>();
    spacePath.mkdir();
    this.refs = 0;
    cleanup = new ArrayList<>();
    this.lastActivity = System.currentTimeMillis();
    this.hasActivityToFlush = true;
  }

  private void resetLastActivity() {
    this.lastActivity = System.currentTimeMillis();
    this.hasActivityToFlush = true;
  }

  public long age() {
    return System.currentTimeMillis() - lastActivity;
  }

  public boolean hasActivityToFlush() {
    return hasActivityToFlush;
  }

  public void attach(PostFlushCleanupEvent event) {
    this.cleanup.add(event);
  }

  public void incRef() {
    resetLastActivity();
    this.refs++;
  }

  public void decRef() {
    resetLastActivity();
    this.refs--;
  }

  public void apply(WriteAheadMessage.Initialize init) {
    this.loaded = true;
    document = init.initialize.redo;
    seq = init.initialize.seq_end;
    this.assetBytes = init.initialize.dAssetBytes;
    this.redoLog.clear();
    this.undoStack.clear();
    this.undoHistory.clear();
    resetLastActivity();
  }

  public boolean canPatch(int seq) {
    return loaded && this.seq + 1 == seq;
  }

  public void apply(WriteAheadMessage.Patch patch) {
    for (WriteAheadMessage.Change change : patch.changes) {
      if (change.seq_begin >= seq + 1) {
        seq = change.seq_end;
        redoLog.add(new Redo(change.redo, seq));
        undoStack.push(new Undo(change.undo, seq));
        active = change.active;
        assetBytes += change.dAssetBytes;
      }
    }
    resetLastActivity();
  }

  public void apply(WriteAheadMessage.Snapshot snapshot) {
    if (snapshot.seq <= this.seq) {
      this.document = snapshot.document;
      this.history = snapshot.history;
      Iterator<Redo> it = redoLog.iterator();
      while (it.hasNext()) {
        Redo redo = it.next();
        if (redo.seq <= snapshot.seq) {
          it.remove();
        }
      }
    }
    resetLastActivity();
  }

  public void delete() {
    this.loaded = false;
    this.reset = true;
    this.document = null;
    this.seq = -1;
    this.assetBytes = 0L;
    this.redoLog.clear();
    this.undoStack.clear();
    this.undoHistory.clear();
    resetLastActivity();
  }

  private void compact() {
    if (redoLog.size() > 0) {
      AutoMorphicAccumulator<String> merge = JsonAlgebra.mergeAccumulator();
      merge.next(document);
      for (Redo redo : redoLog) {
        seq = redo.seq;
        merge.next(redo.redo);
      }
      redoLog.clear();
      document = merge.finish();
      resetLastActivity();
    }
  }

  public boolean isActive() {
    return active;
  }

  public boolean refZero() {
    return refs == 0;
  }

  public boolean isAvailable() {
    if (reset) {
      return false;
    }
    if (loaded) {
      return true;
    }
    return suffixFile("SNAPSHOT").exists();
  }

  public int holding() {
    return 1 + undoHistory.size() + undoStack.size();
  }

  public LocalDocumentChange get() {
    compact();
    return new LocalDocumentChange(document, holding());
  }

  public boolean canInitialize() {
    if (loaded) {
      return false;
    }
    if (suffixFile("SNAPSHOT").exists()) {
      return false;
    }
    return true;
  }

  public void loadIfNotLoaded() throws IOException {
    if (!loaded) {
      load();
    }
  }

  public boolean ensureLoaded(Callback<?> callback) {
    if (loaded) {
      return true;
    }
    try {
      load();
      return true;
    } catch (IOException io) {
      LOGGER.error("failed-to-load", io);
      callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_DISK_CANT_LOAD_IOEXCEPTION, io));
      return false;
    }
  }

  public void load() throws IOException {
    FileInputStream input = new FileInputStream(suffixFile("SNAPSHOT"));
    try {
      SnapshotFileStreamEvents.read(new DataInputStream(new BufferedInputStream(input)), new SnapshotFileStreamEvents() {
        @Override
        public boolean onHeader(SnapshotHeader header) throws IOException {
          history = header.history;
          seq = header.seq;
          active = header.active;
          assetBytes = header.assetBytes;
          return true;
        }

        @Override
        public boolean onDocument(byte[] documentBytes) throws IOException {
          document = new String(documentBytes, StandardCharsets.UTF_8);
          undoHistory = new ArrayList<>();
          return true;
        }

        @Override
        public boolean onUndo(int seq, String undo) throws IOException {
          undoHistory.add(new Undo(undo, seq));
          return true;
        }

        @Override
        public void onFinished(boolean complete) throws IOException {

        }
      });
    } finally {
      input.close();
    }
    loaded = true;
    resetLastActivity();

  }

  public String computeRewind(int seq) {
    ArrayList<Undo> todo = new ArrayList<>();
    boolean found = false;

    for (Undo undo : undoStack) {
      if (undo.seq >= seq) {
        todo.add(undo);
        if (undo.seq == seq) {
          found = true;
        }
      } else {
        break;
      }
    }
    for (Undo undo : undoHistory) {
      if (undo.seq >= seq) {
        todo.add(undo);
        if (undo.seq == seq) {
          found = true;
        }
      } else {
        break;
      }
    }
    if (found) {
      AutoMorphicAccumulator<String> merge = JsonAlgebra.mergeAccumulator(true);
      for (Undo undo : todo) {
        merge.next(undo.undo);
      }
      return merge.finish();
    }
    return null;
  }

  public String patchHead(int seq) {
    AutoMorphicAccumulator<String> merge = JsonAlgebra.mergeAccumulator();
    boolean found = false;
    for (Redo redo : redoLog) {
      if (found) {
        merge.next(redo.redo);
      }
      if (redo.seq == seq) {
        found = true;
      }
    }
    if (merge.empty()) {
      return null;
    } else {
      return merge.finish();
    }
  }

  public void flush() throws IOException {
    if (reset) {
      File snapshotToDelete = suffixFile("SNAPSHOT");
      snapshotToDelete.delete();
      reset = false;
    }
    if (!loaded) {
      return;
    }

    // compact the data
    compact();
    File toWrite = suffixFile("SNAPSHOT.temp");
    byte[] documentBytes = document.getBytes(StandardCharsets.UTF_8);
    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(toWrite), 64 * 1024);
    ArrayList<Undo> newUndoHistory = new ArrayList<>();
    try {
      SnapshotFileStreamEvents writer = SnapshotFileStreamEvents.writerFor(new DataOutputStream(output));
      writer.onHeader(new SnapshotHeader(seq, history, documentBytes.length, assetBytes, active));
      writer.onDocument(documentBytes);
      Iterator<Undo> undoIt = undoStack.iterator();
      while (undoIt.hasNext() && newUndoHistory.size() < history) {
        Undo undo = undoIt.next();
        writer.onUndo(undo.seq, undo.undo);
        newUndoHistory.add(undo);
      }
      for (Undo undo : undoHistory) {
        if (newUndoHistory.size() < history) {
          writer.onUndo(undo.seq, undo.undo);
          newUndoHistory.add(undo);
        } else {
          break;
        }
      }
      writer.onFinished(true);
    } finally {
      output.close();
    }

    Files.move(toWrite.toPath(), suffixFile("SNAPSHOT").toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    undoHistory = newUndoHistory;
    undoStack.clear();
    for (PostFlushCleanupEvent post: cleanup) {
      post.finished();
    }
    cleanup.clear();
    resetLastActivity();
    this.hasActivityToFlush = false;
  }
}
