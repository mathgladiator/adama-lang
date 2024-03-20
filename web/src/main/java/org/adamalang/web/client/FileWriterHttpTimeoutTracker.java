/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.web.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/** this is a temporary file to figure out WTF is causing restores from timing out */
public class FileWriterHttpTimeoutTracker {
  private static final Logger LOG = LoggerFactory.getLogger(FileWriterHttpTimeoutTracker.class);
  private final AtomicBoolean finished;

  public final AtomicBoolean started;
  public final AtomicInteger started_status;
  public final AtomicBoolean body_start;
  public final AtomicLong body_size;
  public final AtomicLong left;
  public final AtomicBoolean body_end;
  public final AtomicInteger error;

  public FileWriterHttpTimeoutTracker() {
    this.finished = new AtomicBoolean(false);
    this.started = new AtomicBoolean(false);
    this.started_status = new AtomicInteger(0);
    this.body_start = new AtomicBoolean(false);
    this.body_size = new AtomicLong(0);
    this.left = new AtomicLong(0);
    this.body_end = new AtomicBoolean(false);
    this.error = new AtomicInteger(-1);
  }

  public void finish() {
    finished.set(true);
  }

  public void audit() {
    if (!finished.get()) {
      LOG.error("file-writer-not-finished: [started=" + started.get() + "] [status=" + started_status.get() + "] [bodystart=" + body_start.get() + "] [bodysize=" + body_size.get() + "] [left=" + left.get() + "] [end=" + body_end.get() + "] [error=" + error.get() + "]");
    }
  }
}
