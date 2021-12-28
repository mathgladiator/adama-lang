package org.adamalang.mysql.mocks;

import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

public class SimpleDataCallback implements Callback<DataService.LocalDocumentChange> {
    private boolean success;
    private int count;
    private int reason;
    public String value;

    public SimpleDataCallback() {
        this.success = false;
        this.count = 0;
        this.reason = 0;
    }

    @Override
    public void success(DataService.LocalDocumentChange value) {
        this.value = value.patch;
        count++;
        success = true;
    }

    @Override
    public void failure(ErrorCodeException ex) {
        count++;
        success = false;
        reason = ex.code;
    }

    public void assertSuccess() {
        Assert.assertEquals(1, count);
        Assert.assertTrue(success);
    }

    public void assertFailure(int code) {
        Assert.assertEquals(1, count);
        Assert.assertFalse(success);
        Assert.assertEquals(code, this.reason);
    }
}
