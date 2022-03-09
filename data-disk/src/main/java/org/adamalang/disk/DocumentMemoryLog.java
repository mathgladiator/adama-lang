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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.disk.files.SnapshotFileStreamEvents;
import org.adamalang.disk.files.SnapshotHeader;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.json.JsonAlgebra;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class DocumentMemoryLog {
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
  private final File spacePath;
  private final String key;

  // is the file loaded, alive, in need of a reset
  private boolean loaded;
  private boolean alive;
  private boolean reset;

  // document state
  private String document;
  private int seq;
  private int history;
  private boolean active;
  private ArrayList<Redo> redoLog;
  private Stack<Undo> undoStack;
  private ArrayList<Undo> undoHistory;

  private File suffixFile(String suffix) {
    return new File(spacePath, key + "." + suffix);
  }

  public DocumentMemoryLog(File spacePath, String key) {
    this.spacePath = spacePath;
    this.key = key;
    this.loaded = false;
    this.alive = suffixFile("SNAPSHOT").exists();
    this.reset = false;
    this.document = null;
    this.seq = -1;
    this.history = 10000;
    this.active = true;
    this.redoLog = new ArrayList<>();
    this.undoStack = new Stack<>();
    this.undoHistory = new ArrayList<>();
  }

  public void initialize(WriteAheadMessage.Initialize init) {
    this.loaded = true;
    this.alive = true;
    document = init.initialize.redo;
    seq = init.initialize.seq_end;
    this.redoLog.clear();
    this.undoStack.clear();
    this.undoHistory.clear();
  }

  public void patch(WriteAheadMessage.Patch patch) {
    for (WriteAheadMessage.Change change : patch.changes) {
      if (change.seq_begin == seq + 1) {
        seq = change.seq_end;
        redoLog.add(new Redo(change.redo, seq));
        undoStack.push(new Undo(change.undo, seq));
        active = change.active;
      }
    }
  }

  public void delete() {
    this.loaded = false;
    this.alive = false;
    this.reset = true;
    this.document = null;
    this.seq = -1;
    this.redoLog.clear();
    this.undoStack.clear();
    this.undoHistory.clear();
  }

  public void snapshot(WriteAheadMessage.Snapshot snapshot) {
    this.document = snapshot.document;
    this.seq = snapshot.seq;
    this.history = snapshot.history;
    this.redoLog.clear();
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
    }
  }

  public boolean get_IsDeleted() {
    return !alive;
  }

  public LocalDocumentChange get_Load() throws IOException {
    if (!loaded) {
      load();
    }
    int redoLogSize = redoLog.size();
    if (redoLogSize == 0) {
      return new LocalDocumentChange(document, 1);
    } else {
      compact();
      return new LocalDocumentChange(document, 1);
    }
  }

  public boolean canInitialize() {
    if (loaded) {
      return false;
    }
    return !suffixFile("SNAPSHOT").exists();
  }

  public boolean canPatch(int seq) {
    return loaded && this.seq + 1 == seq;
  }

  public boolean ensureLoaded(Callback<?> callback) {
    if (loaded) {
      return true;
    }
    try {
      load();
      return true;
    } catch (IOException io) {
      callback.failure(new ErrorCodeException(-1, io));
      return false;
    }
  }

  public void load() throws IOException {
    FileInputStream input = new FileInputStream(suffixFile("SNAPSHOT"));
    try {
      SnapshotFileStreamEvents.read(new DataInputStream(input), new SnapshotFileStreamEvents() {
        @Override
        public boolean onHeader(SnapshotHeader header) throws IOException {
          history = header.history;
          seq = header.seq;
          active = header.active;
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
  }

  public void flush() throws IOException {
    // compact the data
    compact();
    File toWrite = suffixFile("SNAPSHOT.temp");
    byte[] documentBytes = document.getBytes(StandardCharsets.UTF_8);
    FileOutputStream output = new FileOutputStream(toWrite);
    ArrayList<Undo> newUndoHistory = new ArrayList<>();
    try {
      SnapshotFileStreamEvents writer = SnapshotFileStreamEvents.writerFor(new DataOutputStream(output));
      writer.onHeader(new SnapshotHeader(seq, history, documentBytes.length, active));
      writer.onDocument(documentBytes);
      while (!undoStack.empty() && newUndoHistory.size() < history) {
        Undo undo = undoStack.pop();
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
  }
}
