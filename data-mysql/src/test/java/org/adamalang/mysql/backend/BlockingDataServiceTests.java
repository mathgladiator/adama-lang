package org.adamalang.mysql.backend;

import org.adamalang.ErrorCodes;
import org.adamalang.mysql.Base;
import org.adamalang.mysql.BaseConfig;
import org.adamalang.mysql.BaseConfigTests;
import org.adamalang.mysql.mocks.MockActiveKeyStream;
import org.adamalang.mysql.mocks.SimpleDataCallback;
import org.adamalang.mysql.mocks.SimpleMockCallback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class BlockingDataServiceTests {
    private static final Key KEY_1 = new Key("space", "key1");
    private static final DataService.RemoteDocumentUpdate UPDATE_1 = new DataService.RemoteDocumentUpdate(1, NtClient.NO_ONE, "REQUEST", "{\"x\":1,\"y\":4}", "{\"x\":0,\"y\":0}", false, 0);
    private static final DataService.RemoteDocumentUpdate UPDATE_2 = new DataService.RemoteDocumentUpdate(2, NtClient.NO_ONE, "REQUEST", "{\"x\":2}", "{\"x\":1,\"z\":42}", true, 0);

    @Test
    public void flow_1() throws Exception {
        BaseConfig baseConfig = BaseConfigTests.getLocalIntegrationConfig();
        try (Base base = new Base(baseConfig)) {
            DataServiceInstaller installer = new DataServiceInstaller(base);
            try {
                // make sure the database and tables are all proper and set
                installer.install();
                BlockingDataService service = new BlockingDataService(base);

                // create the key the first time, should work
                SimpleMockCallback cb1 = new SimpleMockCallback();
                service.initialize(KEY_1, UPDATE_1, cb1);
                cb1.assertSuccess();

                // second time to create the key should fail
                SimpleMockCallback cb2 = new SimpleMockCallback();
                service.initialize(KEY_1, UPDATE_1, cb2);
                cb2.assertFailure(667658);

                // the key was created in an inactive state, shouldn't scan as active
                {
                    MockActiveKeyStream aks = new MockActiveKeyStream();
                    service.scan(aks);
                    aks.assertFinished(0);
                }

                // update the key and put it in an active state
                SimpleMockCallback cb3 = new SimpleMockCallback();
                service.patch(KEY_1, UPDATE_2, cb3);
                cb3.assertSuccess();

                // it should pop up of a scan now
                {
                    MockActiveKeyStream aks = new MockActiveKeyStream();
                    service.scan(aks);
                    aks.assertFinished(1);
                    aks.assertHas(KEY_1);
                }

                // patching with same sequencer should fail
                SimpleMockCallback cb4 = new SimpleMockCallback();
                service.patch(KEY_1, UPDATE_2, cb4);
                cb4.assertFailure(621580);

                // getting the data should return a composite
                SimpleDataCallback cb5 = new SimpleDataCallback();
                service.get(KEY_1, cb5);
                cb5.assertSuccess();
                Assert.assertEquals("{\"x\":2,\"y\":4}", cb5.value);

                SimpleDataCallback cb6 = new SimpleDataCallback();
                service.compute(KEY_1, DataService.ComputeMethod.Rewind, 1, cb6);
                cb6.assertSuccess();
                Assert.assertEquals("{\"x\":0,\"z\":42,\"y\":0}", cb6.value);

                SimpleDataCallback cb7 = new SimpleDataCallback();
                service.compute(KEY_1, DataService.ComputeMethod.Rewind, 2, cb7);
                cb7.assertSuccess();
                Assert.assertEquals("{\"x\":1,\"z\":42}", cb7.value);

                SimpleDataCallback cb8 = new SimpleDataCallback();
                service.compute(KEY_1, DataService.ComputeMethod.Rewind, 20, cb8);
                cb8.assertFailure(694287);

                SimpleDataCallback cb9 = new SimpleDataCallback();
                service.compute(KEY_1, DataService.ComputeMethod.Unsend, 20, cb9);
                cb9.assertFailure(650252);

                SimpleDataCallback cb10 = new SimpleDataCallback();
                service.compute(KEY_1, DataService.ComputeMethod.Unsend, 1, cb10);
                cb10.assertSuccess();
                Assert.assertEquals("{\"y\":0}", cb10.value);

                SimpleDataCallback cb11 = new SimpleDataCallback();
                service.compute(KEY_1, DataService.ComputeMethod.Patch, 1, cb11);
                cb11.assertSuccess();
                Assert.assertEquals("{\"x\":2}", cb11.value);

                SimpleDataCallback cb12 = new SimpleDataCallback();
                service.compute(KEY_1, null, 1, cb12);
                cb12.assertFailure(656396);

                SimpleMockCallback cb13 = new SimpleMockCallback();
                service.delete(KEY_1, cb13);
                cb13.assertSuccess();

                SimpleMockCallback cb14 = new SimpleMockCallback();
                service.delete(KEY_1, cb14);
                cb14.assertFailure(625676);

                {
                    MockActiveKeyStream aks = new MockActiveKeyStream();
                    aks.crashFinish = true;
                    service.scan(aks);
                    aks.assertFailure(ErrorCodes.SCAN_FAILURE);
                }
            } finally {
                installer.uninstall();
            }
        }
    }
}
