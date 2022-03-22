package org.adamalang.bald.organization;

import io.netty.buffer.ByteBuf;

public class Heap {

  private class FreeSpace {
    private long start;
    private long size;
    private FreeSpace prior;
    private FreeSpace next;

    private void dump() {
      System.out.print("[" + start + "+" + size + ")");
    }
  }

  private FreeSpace head;

  public Heap(long maximumSize) {
    head = new FreeSpace();
    head.start = 0;
    head.size = maximumSize;
  }

  public static class Region {
    public final long position;
    public final int size;
    private boolean freed;

    public Region(long position, int size) {
      this.position = position;
      this.size = size;
      this.freed = false;
    }
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

  public void dump() {
    FreeSpace current = head;
    while (current != null) {
      current.dump();
      current = current.next;
    }
    System.out.println();
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
