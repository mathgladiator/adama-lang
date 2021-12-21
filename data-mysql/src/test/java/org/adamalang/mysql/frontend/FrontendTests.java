package org.adamalang.mysql.frontend;

import org.adamalang.mysql.Base;
import org.adamalang.mysql.BaseConfig;
import org.adamalang.mysql.BaseConfigTests;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class FrontendTests {

    @Test
    public void flow_2() throws Exception {
        BaseConfig baseConfig = BaseConfigTests.getLocalIntegrationConfig();
        try (Base base = new Base(baseConfig)) {
            ManagementInstaller installer = new ManagementInstaller(base);
            try {
                installer.install();
                Assert.assertEquals(1, Users.getOrCreateUserId(base, "x@x.com"));
                Assert.assertEquals(1, Users.getOrCreateUserId(base, "x@x.com"));
                Users.addKey(base, 1, "key", new Date(System.currentTimeMillis() + 1000 * 60));


                Users.removeAllKeys(base, 1);


            } finally {
                installer.uninstall();
            }
        }
    }
}
