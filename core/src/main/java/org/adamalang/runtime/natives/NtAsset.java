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
package org.adamalang.runtime.natives;

import java.util.Objects;

/** represents an asset/file/object that has been attached to a document held in storage */
public class NtAsset implements Comparable<NtAsset> {
  public static final NtAsset NOTHING = new NtAsset("", "", "", 0, "", "");
  public final String id;
  public final String name;
  public final String contentType;
  public final long size;
  public final String md5;
  public final String sha384;

  public NtAsset(String id, String name, String contentType, long size, String md5, String sha384) {
    this.id = id;
    this.name = name;
    this.contentType = contentType;
    this.size = size;
    this.md5 = md5;
    this.sha384 = sha384;
  }

  public String id() {
    return id;
  }

  public boolean valid() {
    return id.length() > 0;
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

  public String md5() {
    return md5;
  }

  public String sha384() {
    return sha384;
  }

  @Override
  public int compareTo(NtAsset o) {
    return id.compareTo(o.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, contentType, size, md5, sha384);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NtAsset ntAsset = (NtAsset) o;
    return id.equals(ntAsset.id) && size == ntAsset.size && Objects.equals(name, ntAsset.name) && Objects.equals(contentType, ntAsset.contentType) && Objects.equals(md5, ntAsset.md5) && Objects.equals(sha384, ntAsset.sha384);
  }

  public long memory() {
    return (id.length() + name.length() + contentType.length() + md5.length() + sha384.length()) * 2L + 48;
  }
}
