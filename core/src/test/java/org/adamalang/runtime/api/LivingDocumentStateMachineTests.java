/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

import org.adamalang.runtime.RealDocumentSetup;
import org.adamalang.runtime.mocks.MockApiResponder;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class LivingDocumentStateMachineTests {
    private static NtClient A = new NtClient("A", "A");

    @Test
    public void get() throws Exception{
        RealDocumentSetup setup = new RealDocumentSetup("public int x; @construct { x = 42; } @connected(who) { return true; }");
        setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123");
        LivingDocumentStateMachine sm = new LivingDocumentStateMachine(setup.transactor);
        MockApiResponder responder = new MockApiResponder();
        sm.get(NtClient.NO_ONE, responder);
        responder.assertSize(1);
        Assert.assertEquals("{\"data\":{\"x\":42},\"outstanding\":[],\"blockers\":[]}", responder.get(0).toString());
        sm.close();
    }

    @Test
    public void getSubscribeMultipleThenLeave() throws Exception{
        RealDocumentSetup setup = new RealDocumentSetup("public int x; @construct { x = 42; } @connected(who) { x++; return true; }");
        setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123");
        LivingDocumentStateMachine sm = new LivingDocumentStateMachine(setup.transactor);
        Assert.assertEquals(0, sm.getResponderCount());
        MockApiResponder responder1 = new MockApiResponder();
        sm.getAndSubscribe(NtClient.NO_ONE, responder1);
        Assert.assertEquals(1, sm.getResponderCount());
        responder1.assertSize(1);
        Assert.assertEquals("{\"data\":{\"x\":43},\"outstanding\":[],\"blockers\":[]}", responder1.get(0).toString());
        MockApiResponder responder2 = new MockApiResponder();
        sm.getAndSubscribe(A, responder2);
        Assert.assertEquals(2, sm.getResponderCount());
        responder2.assertSize(1);
        Assert.assertEquals("{\"data\":{\"x\":44},\"outstanding\":[],\"blockers\":[]}", responder2.get(0).toString());
        responder1.assertSize(2);
        Assert.assertEquals("{\"data\":{\"x\":44},\"outstanding\":[],\"blockers\":[]}", responder1.get(1).toString());
        sm.leave(NtClient.NO_ONE);
        responder2.assertSize(2);
        Assert.assertEquals("{\"data\":{\"x\":44},\"outstanding\":[],\"blockers\":[]}", responder1.get(1).toString());
        Assert.assertEquals(1, sm.getResponderCount());
        sm.leave(A);
        responder1.assertSize(2);
        responder2.assertSize(2);
        Assert.assertEquals(0, sm.getResponderCount());
        sm.close();
    }

    @Test
    public void send() throws Exception{
        RealDocumentSetup setup = new RealDocumentSetup("public int x; @construct { x = 42; } @connected(who) { return true; } message M { int z; } channel foo(M m) { x += m.z; }");
        setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123");
        LivingDocumentStateMachine sm = new LivingDocumentStateMachine(setup.transactor);
        MockApiResponder responder = new MockApiResponder();
        sm.getAndSubscribe(NtClient.NO_ONE, responder);
        responder.assertSize(1);
        Assert.assertEquals("{\"data\":{\"x\":42},\"outstanding\":[],\"blockers\":[]}", responder.get(0).toString());
        sm.send(NtClient.NO_ONE, "foo", Utility.parseJsonObject("{\"z\":100}"));
        responder.assertSize(2);
        Assert.assertEquals("{\"data\":{\"x\":142},\"outstanding\":[],\"blockers\":[]}", responder.get(1).toString());
        sm.close();
    }

}
