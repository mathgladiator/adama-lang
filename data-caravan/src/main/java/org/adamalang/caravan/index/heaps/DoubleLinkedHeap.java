package org.adamalang.caravan.index.heaps;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Region;

/** a very simple doubly-linked heap */
public class DoubleLinkedHeap implements Heap {
  public final long maximumSize;

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
  public DoubleLinkedHeap(long maximumSize) {
    this.maximumSize = maximumSize;
    this.head = new FreeSpace();
    this.head.start = 0;
    this.head.size = maximumSize;
  }

  @Override
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

  @Override
  public void free(Region region) {
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
            hole.prior.next = hole;
          } else {
            head = hole;
          }
        }
        return;
      } // else, skip it
      current = current.next;
    }
  }

  @Override
  public void snapshot(ByteBuf buf) {
    FreeSpace current = head;
    while (current != null) {
      buf.writeBoolean(true);
      buf.writeLongLE(current.start);
      buf.writeLongLE(current.size);
      current = current.next;
    }
    buf.writeBoolean(false);
  }

  @Override
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
