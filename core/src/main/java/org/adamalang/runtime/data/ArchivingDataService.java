/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

/** a data service which backup data */
public interface ArchivingDataService extends DataService {
  /** restore a file (must be idempotent) */
  void restore(Key key, String archiveKey, Callback<Void> callback);

  /** backup a document, returning an archiveKey */
  void backup(Key key, Callback<BackupResult> callback);

  /** delete an overwritten back up */
  void cleanUp(Key key, String archiveKey);
}
