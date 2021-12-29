/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
