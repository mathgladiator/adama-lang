/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

import java.util.List;

/** an interface to describe how to find a specific document by key */
public interface FinderService extends SimpleFinderService {

  /** take over for the key */
  void bind(Key key, Callback<Void> callback);

  /** find the result and bind it to me */
  void findbind(Key key, Callback<DocumentLocation> callback);

  /** release the machine for the given key */
  void free(Key key, Callback<Void> callback);

  /** set a backup copy while still active on machine */
  void backup(Key key, BackupResult result, Callback<Void> callback);

  /** mark the key for deletion */
  void markDelete(Key key, Callback<Void> callback);

  /** signal that deletion has been completed */
  void commitDelete(Key key, Callback<Void> callback);

  /** list all items on a host */
  void list(Callback<List<Key>> callback);

  /** list all items on a host */
  void listDeleted(Callback<List<Key>> callback);

}
