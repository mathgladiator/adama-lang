/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.data.disk;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.function.Consumer;

public class FileSystemDataServiceTests {
  private void d(String testname, Consumer<FileSystemDataService> test) throws Exception {
    final var root = new File(testname + ".dir");
    System.err.println(root.getAbsolutePath());
    root.mkdir();
    try {
      test.accept(new FileSystemDataService(root));
    } finally {
      try {
        for (File f : root.listFiles()) {
          f.delete();
        }
      } finally {
        root.delete();
      }
    }
  }

  private static class LocalCallback<T> implements Callback<T> {
    private T value = null;
    private Exception ex;

    public T get() {
      if (ex == null && value != null) {
        return value;
      } else {
        throw new RuntimeException(ex);
      }
    }

    @Override
    public void success(T value) {
      this.value = value;
    }

    @Override
    public void failure(ErrorCodeException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Test
  public void testFlow() throws Exception {
    d("test_flow", (f) -> {
      LocalCallback<Long> id = new LocalCallback<>();
      f.create(id);
      LocalCallback<DataService.LocalDocumentChange> fetch = new LocalCallback<>();
      f.get(id.get(), fetch);
      Assert.assertEquals("{}", fetch.get().patch);
      LocalCallback<Void> written = new LocalCallback<>();
      f.patch(id.get(), new DataService.RemoteDocumentUpdate(1, NtClient.NO_ONE, null, "{}", "{\"x\":123}", "{\"x\":null}", false, 0), written);
      f.patch(id.get(), new DataService.RemoteDocumentUpdate(1, NtClient.NO_ONE, null, "{}", "{\"x\":42}", "{\"x\":123}", false, 0), written);
      f.get(id.get(), fetch);
      Assert.assertEquals("{\"x\":42}", fetch.get().patch);
    });
  }

  @Test
  public void coverage() throws Exception {
    d("crash", (f) -> {
      try {
        f.fork(0, 0, null, null, null);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
      }

      try {
        f.rewind(0, null,null, null);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
      }
      try {
        f.unsend(0, null, null, null);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
      }
    });
  }
}
