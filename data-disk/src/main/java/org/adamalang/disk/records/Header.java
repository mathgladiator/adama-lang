package org.adamalang.disk.records;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/** every file has a header which is used for versioning should things go south */
public class Header {
  public static final int CURRENT_VERSION = 424242;
  public final int version;

  private Header(int version) {
    this.version = version;
  }

  public static void writeNewHeader(DataOutputStream output) throws IOException {
    output.writeInt(CURRENT_VERSION);
  }

  public static Header from(DataInputStream input) throws IOException {
    return new Header(input.readInt());
  }
}
