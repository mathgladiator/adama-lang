/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.data;

import org.adamalang.caravan.index.Region;

import java.io.IOException;

/** combine multiple storages into one */
public class SequenceStorage implements Storage {
  private final Storage[] storages;
  private final long size;

  public SequenceStorage(Storage... storages) {
    this.storages = storages;
    long _size = 0;
    for (Storage storage : storages) {
      _size += storage.size();
    }
    this.size = _size;
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public void write(Region region, byte[] mem) {
    long at = region.position;
    for (Storage storage : storages) {
      if (at < storage.size()) {
        storage.write(new Region(at, region.size), mem);
        return;
      }
      at -= storage.size();
    }
  }

  @Override
  public byte[] read(Region region) {
    long at = region.position;
    for (Storage storage : storages) {
      if (at < storage.size()) {
        return storage.read(new Region(at, region.size));
      }
      at -= storage.size();
    }
    return null;
  }

  @Override
  public void flush() throws IOException {
    for (Storage storage : storages) {
      storage.flush();
    }
  }

  @Override
  public void close() throws IOException {
    for (Storage storage : storages) {
      storage.close();
    }
  }
}
