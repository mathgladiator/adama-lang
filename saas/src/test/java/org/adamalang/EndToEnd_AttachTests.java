/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang;

import org.adamalang.common.Hashing;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Iterator;

public class EndToEnd_AttachTests {

  @Test
  public void cantAttach() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      Iterator<String> c0 = fe.execute("{}");
      Assert.assertEquals("ERROR:233120", c0.next());
      String devIdentity = fe.setupDevIdentity();
      Iterator<String> c3 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/create\",\"space\":\"newspace\"}");
      Assert.assertEquals("FINISH:{}", c3.next());
      Iterator<String> c4  =
          fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/set\",\"space\":\"newspace\",\"plan\":"+ EndToEnd_SpaceInfoTests.planFor(
              "@static { create { return true; } }" +
                  "@connected { return true; }" +
                  "public int x = 1;" +
                  "message M { int z; }" +
                  "channel foo(M m) { x += m.z; }"
          ) + "}");
      Assert.assertEquals("FINISH:{}", c4.next());
      Iterator<String> c5 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/create\",\"space\":\"newspace\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("FINISH:{}", c5.next());
      Iterator<String> c6 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"attachment/start\",\"space\":\"newspace\",\"key\":\"a\",\"filename\":\"thefilename\",\"content-type\":\"text/plain\"}");
      Assert.assertEquals("ERROR:966768", c6.next());
    }
  }

  @Test
  public void canAttach() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      for (File attachment : fe.attachmentRoot.listFiles()) {
        if (attachment.getName().endsWith(".inflight")) {
          attachment.delete();
        }
      }
      Assert.assertEquals(0, fe.attachmentRoot.listFiles().length);
      String devIdentity = fe.setupDevIdentity();
      Iterator<String> c3 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/create\",\"space\":\"newspace\"}");
      Assert.assertEquals("FINISH:{}", c3.next());
      Iterator<String> c4  =
          fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/set\",\"space\":\"newspace\",\"plan\":"+ EndToEnd_SpaceInfoTests.planFor(
              "@static { create { return true; } }" +
                  "@connected { return true; }" +
                  "@can_attach { return true; }" +
                  "@attached(what) { }" +
                  "public int x = 1;" +
                  "message M { int z; }" +
                  "channel foo(M m) { x += m.z; }"
          ) + "}");
      Assert.assertEquals("FINISH:{}", c4.next());
      Iterator<String> c5 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/create\",\"space\":\"newspace\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("FINISH:{}", c5.next());
      Iterator<String> c6 = fe.execute("{\"id\":100,\"identity\":\"" + devIdentity + "\",\"method\":\"attachment/start\",\"space\":\"newspace\",\"key\":\"a\",\"filename\":\"thefilename\",\"content-type\":\"text/plain\"}");
      Assert.assertEquals("STREAM:{\"chunk_request_size\":65536}", c6.next());
      Assert.assertEquals(1, fe.attachmentRoot.listFiles().length);
      byte[] chunkToSend = "[This is a chunk]".getBytes(StandardCharsets.UTF_8);

      for (int k = 0; k < 10; k++) {
        Iterator<String> c7 = fe.execute("{\"id\":8,\"upload\":100,\"identity\":\"" + devIdentity + "\",\"method\":\"attachment/append\"," + partialJsonChunk(chunkToSend, false) + "}");
        Assert.assertEquals("FINISH:{}", c7.next());
        Assert.assertEquals("STREAM:{\"chunk_request_size\":65536}", c6.next());
      }

      Iterator<String> c8 = fe.execute("{\"id\":8,\"upload\":100,\"identity\":\"" + devIdentity + "\",\"method\":\"attachment/finish\"}");
      Assert.assertEquals("FINISH:{\"assetId\":", c8.next().substring(0, 18));
      Assert.assertEquals("FINISH:null", c6.next());
      Assert.assertEquals(1, fe.attachmentRoot.listFiles().length);
      File f = fe.attachmentRoot.listFiles()[0];
      Assert.assertEquals("[This is a chunk][This is a chunk][This is a chunk][This is a chunk][This is a chunk][This is a chunk][This is a chunk][This is a chunk][This is a chunk][This is a chunk]", Files.readString(f.toPath()));

      Iterator<String> c9 = fe.execute("{\"id\":8,\"upload\":120,\"identity\":\"" + devIdentity + "\",\"method\":\"attachment/append\"," + partialJsonChunk(chunkToSend, false) + "}");
      Assert.assertEquals("ERROR:477201", c9.next());
      Iterator<String> c10 = fe.execute("{\"id\":8,\"upload\":120,\"identity\":\"" + devIdentity + "\",\"method\":\"attachment/finish\"}");
      Assert.assertEquals("ERROR:478227", c10.next());
      Iterator<String> c11 = fe.execute("{\"id\":125,\"identity\":\"" + devIdentity + "\",\"method\":\"attachment/start\",\"space\":\"newspace\",\"key\":\"a\",\"filename\":\"thefilename\",\"content-type\":\"text/plain\"}");
      Assert.assertEquals("STREAM:{\"chunk_request_size\":65536}", c11.next());
    }
  }

  @Test
  public void attachmentDataCorruption() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      for (File attachment : fe.attachmentRoot.listFiles()) {
        if (attachment.getName().endsWith(".inflight")) {
          attachment.delete();
        }
      }
      Assert.assertEquals(0, fe.attachmentRoot.listFiles().length);
      String devIdentity = fe.setupDevIdentity();
      Iterator<String> c3 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/create\",\"space\":\"newspace\"}");
      Assert.assertEquals("FINISH:{}", c3.next());
      Iterator<String> c4  =
          fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/set\",\"space\":\"newspace\",\"plan\":"+ EndToEnd_SpaceInfoTests.planFor(
              "@static { create { return true; } }" +
                  "@connected { return true; }" +
                  "@can_attach { return true; }" +
                  "@attached(what) { }" +
                  "public int x = 1;" +
                  "message M { int z; }" +
                  "channel foo(M m) { x += m.z; }"
          ) + "}");
      Assert.assertEquals("FINISH:{}", c4.next());
      Iterator<String> c5 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/create\",\"space\":\"newspace\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("FINISH:{}", c5.next());
      Iterator<String> c6 = fe.execute("{\"id\":100,\"identity\":\"" + devIdentity + "\",\"method\":\"attachment/start\",\"space\":\"newspace\",\"key\":\"a\",\"filename\":\"thefilename\",\"content-type\":\"text/plain\"}");
      Assert.assertEquals("STREAM:{\"chunk_request_size\":65536}", c6.next());
      Assert.assertEquals(1, fe.attachmentRoot.listFiles().length);
      byte[] chunkToSend = "[This is a chunk]".getBytes(StandardCharsets.UTF_8);

      Iterator<String> c7 = fe.execute("{\"id\":8,\"upload\":100,\"identity\":\"" + devIdentity + "\",\"method\":\"attachment/append\"," + partialJsonChunk(chunkToSend, true) + "}");
      Assert.assertEquals("ERROR:999472", c7.next());
      Assert.assertEquals("ERROR:920719", c6.next());
      Assert.assertEquals(0, fe.attachmentRoot.listFiles().length);
    }
  }

  private String partialJsonChunk(byte[] chunk, boolean corrupt) {
    JsonStreamWriter writer = new JsonStreamWriter();
    MessageDigest md5chunk = Hashing.md5();
    md5chunk.update(chunk);
    writer.writeObjectFieldIntro("chunk-md5");
    writer.writeString(Hashing.finishAndEncode(md5chunk));
    if (corrupt) {
      chunk[chunk.length / 2]++;
    }
    writer.writeObjectFieldIntro("base64-bytes");
    writer.writeString(Base64.encodeBase64String(chunk));
    return writer.toString();
  }
}
