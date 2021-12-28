package org.adamalang.runtime.sys;

import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.MockRacerLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.NullCallbackLatch;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Test;


public class ServiceCreateTests {
    private static final Key KEY = new Key("space", "key");
    private static final String SIMPLE_CODE_MSG = "public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";

    @Test
    public void create_super_happy() throws Exception {
        LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
        MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
        TimeSource time = new MockTime();
        MockInstantDataService dataService = new MockInstantDataService();
        CoreService service = new CoreService(factoryFactory, dataService, time, 3);
        try {
            NullCallbackLatch created = new NullCallbackLatch();
            service.create(NtClient.NO_ONE, KEY, "{}", null, created);
            created.await_success();
        } finally {
            service.shutdown();
        }
    }

    @Test
    public void cant_create_twice_series() throws Exception {
        LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
        MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
        TimeSource time = new MockTime();
        MockInstantDataService dataService = new MockInstantDataService();
        CoreService service = new CoreService(factoryFactory, dataService, time, 3);
        try {
            NullCallbackLatch created1 = new NullCallbackLatch();
            NullCallbackLatch created2 = new NullCallbackLatch();
            service.create(NtClient.NO_ONE, KEY, "{}", null, created1);
            created1.await_success();
            service.create(NtClient.NO_ONE, KEY, "{}", null, created2);
            created2.await_failure();
        } finally {
            service.shutdown();
        }
    }

    @Test
    public void cant_create_twice_parallel() throws Exception {
        LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
        MockRacerLivingDocumentFactoryFactory factoryFactory = new MockRacerLivingDocumentFactoryFactory();
        TimeSource time = new MockTime();
        MockInstantDataService dataService = new MockInstantDataService();
        CoreService service = new CoreService(factoryFactory, dataService, time, 3);
        try {
            NullCallbackLatch created1 = new NullCallbackLatch();
            NullCallbackLatch created2 = new NullCallbackLatch();
            Runnable latchTwoCalls = factoryFactory.latchAt(2);
            service.create(NtClient.NO_ONE, KEY, "{}", null, created1);
            service.create(NtClient.NO_ONE, KEY, "{}", null, created2);
            latchTwoCalls.run();
            factoryFactory.satisfyAll(KEY, factory);
            created1.await_success();
            created2.await_failure();
        } finally {
            service.shutdown();
        }
    }

    @Test
    public void cant_create_no_factory() throws Exception {
        MockRacerLivingDocumentFactoryFactory factoryFactory = new MockRacerLivingDocumentFactoryFactory();
        TimeSource time = new MockTime();
        MockInstantDataService dataService = new MockInstantDataService();
        CoreService service = new CoreService(factoryFactory, dataService, time, 3);
        try {
            NullCallbackLatch created = new NullCallbackLatch();
            Runnable latchAfter = factoryFactory.latchAt(1);
            service.create(NtClient.NO_ONE, KEY, "{}", null, created);
            latchAfter.run();
            factoryFactory.satisfyNone(KEY);
            created.await_failure();
        } finally {
            service.shutdown();
        }
    }
}
