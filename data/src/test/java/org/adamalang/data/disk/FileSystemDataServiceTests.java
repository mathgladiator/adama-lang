package org.adamalang.data.disk;

import org.adamalang.runtime.contracts.DataCallback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;
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

  private static class LocalDataCallback<T> implements DataCallback<T> {
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
      LocalDataCallback<Long> id = new LocalDataCallback<>();
      f.create(id);
      LocalDataCallback<DataService.LocalDocumentChange> fetch = new LocalDataCallback<>();
      f.get(id.get(), fetch);
      Assert.assertEquals("{}", fetch.get().patch);
      LocalDataCallback<Void> written = new LocalDataCallback<>();
      f.patch(id.get(), new DataService.RemoteDocumentUpdate(1, "{}", "{\"x\":123}", "{\"x\":null}", false, 0), written);
      f.patch(id.get(), new DataService.RemoteDocumentUpdate(1, "{}", "{\"x\":42}", "{\"x\":123}", false, 0), written);
      f.get(id.get(), fetch);
      Assert.assertEquals("{\"x\":42}", fetch.get().patch);
    });
  }

  @Test
  public void coverage() throws Exception {
    d("crash", (f) -> {
      try {
        f.fork(0, 0, 0, null);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
      }

      try {
        f.rewind(0, 0, null);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
      }
      try {
        f.unsend(0, 0, 0, null);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
      }
      try {
        f.delete(0, null);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
      }
    });
  }
}
