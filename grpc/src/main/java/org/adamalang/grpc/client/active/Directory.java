package org.adamalang.grpc.client.active;

import org.adamalang.grpc.client.contracts.CreateCallback;
import org.adamalang.grpc.client.contracts.Events;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;

/** This represents a front-door for the gRPC client that talks with document connection manamagent */
public class Directory {
  public DirectoryBase[] bases;

  public Directory(int nThreads) {
    bases = new DirectoryBase[nThreads];
    for (int k = 0; k < nThreads; k++) {
      bases[k] = new DirectoryBase();
    }
  }

  private DirectoryBase baseOf(Key key) {
    return bases[key.hashCode() % bases.length];
  }

  /** create a document */
  public void create(
      NtClient who,
      Key key,
      String entropy,
      String arg,
      CreateCallback callback) {
     baseOf(key).getOrCreate(key, (sm) -> {
      sm.create(who, entropy, arg, callback);
    });
  }

  /** connect a document */
  public void connect(NtClient who, Key key, Events events) {
    baseOf(key).getOrCreate(key, (sm) -> {
      sm.connect(who, events);
    });
  }

  /** shutdown the client */
  public void shutdown() {
    for (int k = 0; k < bases.length; k++) {
      bases[k].shutdown();
    }
  }
}
