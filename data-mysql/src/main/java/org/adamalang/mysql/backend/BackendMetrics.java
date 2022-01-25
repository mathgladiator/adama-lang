/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.backend;

import org.adamalang.common.metrics.MetricsFactory;

public class BackendMetrics {
  private final Runnable read;
  private final Runnable write;
  public final Runnable lookup;
  public final Runnable lookup_change;
  public final Runnable delete;

  public final Runnable read_get;
  public final Runnable read_head_patch;
  public final Runnable read_rewind;
  public final Runnable read_compact;

  public final Runnable write_init;
  public final Runnable write_patch;
  public final Runnable write_compact;

  public BackendMetrics(MetricsFactory factory) {
    read = factory.counter("mysql_read");
    write = factory.counter("mysql_write");
    lookup = factory.counter("mysql_lookup");
    delete = factory.counter("mysql_delete");
    lookup_change = factory.counter("mysql_lookup_change");

    Runnable _read_get = factory.counter("mysql_read_get");
    this.read_get = () -> {
      _read_get.run();
      read.run();
    };

    Runnable _read_head_patch = factory.counter("mysql_read_head_patch");
    this.read_head_patch = () -> {
      _read_head_patch.run();
      read.run();
    };


    Runnable _read_rewind = factory.counter("mysql_read_rewind");
    this.read_rewind = () -> {
      _read_rewind.run();
      read.run();
    };


    Runnable _read_compact = factory.counter("mysql_read_compact");
    this.read_compact = () -> {
      _read_compact.run();
      read.run();
    };

    Runnable _write_init = factory.counter("mysql_write_init");
    this.write_init = () -> {
      _write_init.run();
      write.run();
    };

    Runnable _write_patch = factory.counter("mysql_write_patch");
    this.write_patch = () -> {
      _write_patch.run();
      write.run();
    };

    Runnable _write_compact = factory.counter("mysql_write_compact");
    this.write_compact = () -> {
      _write_compact.run();
      write.run();
    };
  }
}
