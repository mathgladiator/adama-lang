package org.adamalang.mysql.frontend;

import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class RoleTests {
    @Test
    public void coverage() throws Exception {
        Assert.assertEquals(Role.Developer, Role.from("developer"));
        Assert.assertEquals(Role.None, Role.from("none"));
        try {
            Role.from("ninja-cake-master");
        } catch (ErrorCodeException ex) {
            Assert.assertEquals(688141, ex.code);

        }
    }
}
