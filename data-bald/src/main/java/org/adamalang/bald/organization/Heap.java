/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.organization;

import io.netty.buffer.ByteBuf;

import java.util.concurrent.Executors;

/** a very simple doubly-linked heap */
public class Heap {

  /** a mapping of free space */
  private class FreeSpace {
    private long start;
    private long size;
    private FreeSpace prior;
    private FreeSpace next;

    @Override
    public String toString() {
      return "[" + start + "," + (start + size) + ")";
    }
  }

  /** the head of the linked list */
  private FreeSpace head;

  /** construct the heap as empty with the given maximum size available */
  public Heap(long maximumSize) {
    head = new FreeSpace();
    head.start = 0;
    head.size = maximumSize;
  }

  public Region ask(int size) {
    FreeSpace current = head;
    while (current != null) {
      if (size < current.size) {
        long position = current.start;
        current.start += size;
        current.size -= size;
        return new Region(position, size);
      }
      current = current.next;
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    FreeSpace current = head;
    while (current != null) {
      sb.append(current.toString());
      current = current.next;
    }
    return sb.toString();
  }

  public void free(Region region) {
    if (region.freed) {
      throw new NullPointerException();
    }
    region.freed = true;
    FreeSpace current = head;
    while (current != null) {
      if (region.position + region.size == current.start) {
        // the allocated block touches existing free space, let's grow that freespace
        current.start -= region.size;
        current.size += region.size;

        FreeSpace prior = current.prior;
        if (prior != null && prior.start + prior.size == current.start) {
          // absorb the prior freespace
          current.start -= prior.size;
          current.size += prior.size;
          current.prior = prior.prior;
          // re-link
          if (prior.prior != null) {
            prior.prior.next = current;
          } else {
            head = current;
          }
        }
        return;
      } else if (region.position < current.start) {
        if (current.prior != null && current.prior.start + current.prior.size == region.position) {
          // grow the prior
          current.prior.size += region.size;
        } else {
          // insert a new hole
          FreeSpace hole = new FreeSpace();
          hole.start = region.position;
          hole.size = region.size;
          hole.next = current;
          hole.prior = current.prior;
          current.prior = hole;
          if (hole.prior != null) {
            hole.prior = hole;
          } else {
            head = hole;
          }
        }
        return;
      } // else, skip it
      current = current.next;
    }
  }

  public void snapshot(ByteBuf buf) {
    FreeSpace current = head;
    while (current != null) {
      buf.writeBoolean(true);
      buf.writeLongLE(current.start);
      buf.writeLongLE(current.size);
    }
    buf.writeBoolean(false);
  }

  public void load(ByteBuf buf) {
    this.head = null;
    FreeSpace prior = null;
    while (buf.readBoolean()) {
      FreeSpace current = new FreeSpace();
      current.start = buf.readLongLE();
      current.size = buf.readLongLE();
      current.prior = prior;
      current.next = null;
      if (prior != null) {
        prior.next = current;
      } else {
        head = current;
      }
      prior = current;
    }
  }
}
