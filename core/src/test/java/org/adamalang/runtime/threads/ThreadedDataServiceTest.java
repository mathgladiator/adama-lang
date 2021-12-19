package org.adamalang.runtime.threads;

import org.adamalang.runtime.contracts.ActiveKeyStream;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ThreadedDataServiceTest {

    @Test
    public void coverage() throws Exception {
        MockInstantDataService dataService = new MockInstantDataService();
        ThreadedDataService ds = new ThreadedDataService(1, () -> dataService);
        Key key = new Key("space", "key");
        DataService.RemoteDocumentUpdate update = new DataService.RemoteDocumentUpdate(1, NtClient.NO_ONE, "", "", "", false, 1);
        CountDownLatch latch = new CountDownLatch(6);
        ds.scan(new ActiveKeyStream() {
            @Override
            public void schedule(Key key, long time) {

            }

            @Override
            public void finish() {
                latch.countDown();
            }

            @Override
            public void error(ErrorCodeException failure) {

            }
        });
        ds.get(key, new Callback<DataService.LocalDocumentChange>() {
            @Override
            public void success(DataService.LocalDocumentChange value) {

            }

            @Override
            public void failure(ErrorCodeException ex) {
                latch.countDown();
            }
        });
        ds.initialize(key, update, new Callback<Void>() {
            @Override
            public void success(Void value) {
                latch.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
            }
        });
        ds.patch(key, update, new Callback<Void>() {
            @Override
            public void success(Void value) {
                latch.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
            }
        });
        ds.compute(key, DataService.ComputeMethod.Rewind, 1, new Callback<DataService.LocalDocumentChange>() {
            @Override
            public void success(DataService.LocalDocumentChange value) {
                latch.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
        });
        ds.delete(key, new Callback<Void>() {
            @Override
            public void success(Void value) {
                latch.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
        });
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(ds.shutdown().await(1000, TimeUnit.MILLISECONDS));
    }
}
