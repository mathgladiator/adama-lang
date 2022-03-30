/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

/** write an archive */
public interface ArchiveWriter {
  /** record a patch */
  public void record(RemoteDocumentUpdate patch);

  /** record a snapshot */
  public void snapshot(int seq, String document, int history);

  /** finish recording */
  public void finish();

  public void failed(int errorCode);
}
