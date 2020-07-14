/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.ApiResponder;
import org.adamalang.runtime.exceptions.ApiErrorReason;
import org.junit.Assert;

import java.util.ArrayList;

public class MockApiResponder implements ApiResponder {
    ArrayList<JsonNode> responses;
    private boolean doneAlready;
    private ApiErrorReason errorReason;

    public MockApiResponder() {
        this.responses = new ArrayList<>();
        this.doneAlready = false;
        this.errorReason = null;
    }

    public void assertErrorReason(ApiErrorReason expected) {
        Assert.assertEquals(expected, errorReason);
    }

    public void assertDone() {
        Assert.assertTrue(doneAlready);
    }

    public void assertNotDone() {
        Assert.assertFalse(doneAlready);
    }

    public void assertSize(int sz) {
        Assert.assertEquals(sz, responses.size());
    }

    public ObjectNode get(int idx) {
        Assert.assertTrue(idx < responses.size());
        JsonNode response = responses.get(idx);
        Assert.assertTrue(response instanceof ObjectNode);
        return (ObjectNode) response;
    }

    @Override
    public void respond(JsonNode data, boolean done) {
        this.responses.add(data);
        if (done) {
            setDone();
        }
    }

    @Override
    public void error(ApiErrorReason errorReason) {
        setDone();
        this.errorReason = errorReason;
    }

    private void setDone() {
        if (doneAlready) {
            Assert.fail("already done");
        }
        doneAlready = true;
    }
}
