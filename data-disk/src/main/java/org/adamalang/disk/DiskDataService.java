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
import org.adamalang.runtime.data.*;

import java.io.File;

/** the disk data service which aims for low latency commits */
public class DiskDataService implements DataService {
  private final File root;

  public DiskDataService(File root) {
    this.root = root;
  }

  @Override // GET
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    // if the Integrator has a snapshot if exists in memory
    //    if the snapshot indicates a delete
    //      failure
    //    use the snapshot
    // else if it exists on disk
    //    pull a snapshot from disk
    // else
    //    failed not found
    // pull the sequencer of the snapshot
    // pull updates from the WAL
    // apply updates to the snapshot
  }

  @Override // WRITE
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    // if the integrator has a snapshot in memory,
    //    if the snapshot indicates a delete
    //      success
    //    else
    //      failure
    // if the file exists on disk then failure
    // drop an initialize within the WAL, return success
  }

  @Override // WRITE
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    // drop a patch in the WAL
  }

  @Override // READ
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {

  }

  @Override // WRITE
  public void delete(Key key, Callback<Void> callback) {
    // drop a delete in the WAL
  }

  @Override // WRITE
  public void compactAndSnapshot(Key key, int seq, String snapshot, int history, Callback<Integer> callback) {
    // drop the snapshot in the WAL
  }
}
