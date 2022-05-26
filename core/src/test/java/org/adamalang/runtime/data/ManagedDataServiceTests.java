/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.managed.Base;
import org.adamalang.runtime.data.mocks.SimpleVoidCallback;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.MockArchiveDataServiceWrapper;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ManagedDataServiceTests {
  public static final Key KEY1 = new Key("space", "123");
  public static final Key KEY2 = new Key("space", "1232");
  private static final RemoteDocumentUpdate UPDATE_1 =
      new RemoteDocumentUpdate(
          1, 1, NtClient.NO_ONE, "REQUEST", "{\"x\":1,\"y\":4}", "{\"x\":0,\"y\":0}", false, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_2 =
      new RemoteDocumentUpdate(
          2, 2, null, "REQUEST", "{\"x\":2}", "{\"x\":1,\"z\":42}", true, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_3 =
      new RemoteDocumentUpdate(
          3, 3, null, "REQUEST", "{\"x\":3}", "{\"x\":2,\"z\":42}", true, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_4 =
      new RemoteDocumentUpdate(
          4, 4, null, "REQUEST", "{\"x\":4}", "{\"x\":3,\"z\":42}", true, 0, 100, UpdateType.AddUserData);

  private static class Setup implements AutoCloseable {
    public final MockFinderService finder;
    public final SimpleExecutor executor;
    public final MockInstantDataService data;
    public final MockArchiveDataServiceWrapper archive;
    public final Base base;
    public final ManagedDataService managed;

    public Setup() throws Exception {
      this.finder = new MockFinderService();
      this.executor = SimpleExecutor.create("setup");
      this.data = new MockInstantDataService();
      this.archive = new MockArchiveDataServiceWrapper(data);
      this.base = new Base(new MockFinderService(), archive, "region", "target", executor, 5000);
      this.managed = new ManagedDataService(base);
    }

    @Override
    public void close() throws Exception {
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void flow() throws Exception {
    try (Setup setup = new Setup()) {
      Runnable waitInit = setup.data.latchLogAt(1);
      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY1, UPDATE_1, cb_Init);
        cb_Init.assertSuccess();
      }
      waitInit.run();

    }
  }
}
