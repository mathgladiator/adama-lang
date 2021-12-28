package org.adamalang.runtime.sys;

import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class ServiceDocumentControlTests {
    private static final Key KEY = new Key("space", "key");
    private static final String SIMPLE_CODE_MSG = "public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { Document.rewind(1); }";

    @Test
    public void rewind() throws Exception {
        LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
        MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
        TimeSource time = new MockTime();
        MockInstantDataService dataService = new MockInstantDataService();
        dataService.initialize(KEY, ServiceConnectTests.wrap("{\"__constructed\":true}"), Callback.DONT_CARE_VOID);
        dataService.patch(KEY, ServiceConnectTests.wrap("{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"), Callback.DONT_CARE_VOID);
        CoreService service = new CoreService(factoryFactory, dataService, time, 3);
        try {
            MockStreamback streamback = new MockStreamback();
            Runnable latch1 = streamback.latchAt(2);
            Runnable latch2 = streamback.latchAt(3);
            Runnable latch3 = streamback.latchAt(4);
            service.connect(NtClient.NO_ONE, KEY, streamback);
            streamback.await_began();
            latch1.run();
            Assert.assertEquals("STATUS:Connected", streamback.get(0));
            Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback.get(1));
            LatchCallback cb1 = new LatchCallback();
            streamback.get().send("foo", null, "{}", cb1);
            cb1.await_success(5);
            latch2.run();
            Assert.assertEquals("{\"data\":{\"x\":1000},\"seq\":5}", streamback.get(2));
            streamback.get().disconnect();
            latch3.run();
            Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
        } finally {
            service.shutdown();
        }
    }
}
