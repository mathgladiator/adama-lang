/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

import java.io.File;

/** restore/backup from the cloud */
public interface Cloud {
  /** the path for where cloud files are stored */
  File path();

  /** restore the archive key from the cloud to a local file */
  void restore(Key key, String archiveKey, Callback<File> callback);

  /** backup the given file and send to the cloud */
  void backup(Key key, File archiveFile, Callback<Void> callback);

  /** delete the related archive key */
  void delete(Key key, String archiveKey, Callback<Void> callback);
}
