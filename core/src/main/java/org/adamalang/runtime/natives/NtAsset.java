package org.adamalang.runtime.natives;

import java.util.Objects;

/** represents an asset/file/object that has been attached to a document held in storage */
public class NtAsset implements Comparable<NtAsset> {
  public static final NtAsset NOTHING = new NtAsset(0, "", "", 0, "", "");
  public final long id;
  public final String name;
  public final String contentType;
  public final long size;
  public final String md5;
  public final String sha384;

  public NtAsset(long id, String name, String contentType, long size, String md5, String sha384) {
    this.id = id;
    this.name = name;
    this.contentType = contentType;
    this.size = size;
    this.md5 = md5;
    this.sha384 = sha384;
  }

  public long id() {
    return id;
  }

  public boolean valid() {
    return id > 0;
  }

  public String name() {
    return name;
  }

  public String type() {
    return contentType;
  }

  public long size() {
    return size;
  }

  @Override
  public int compareTo(NtAsset o) {
    return Long.compare(id, o.id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NtAsset ntAsset = (NtAsset) o;
    return id == ntAsset.id && size == ntAsset.size && Objects.equals(name, ntAsset.name) && Objects.equals(contentType, ntAsset.contentType) && Objects.equals(md5, ntAsset.md5) && Objects.equals(sha384, ntAsset.sha384);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, contentType, size, md5, sha384);
  }
}