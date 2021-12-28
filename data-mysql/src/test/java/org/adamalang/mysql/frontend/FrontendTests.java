package org.adamalang.mysql.frontend;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.BaseConfig;
import org.adamalang.mysql.DataBaseConfigTests;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FrontendTests {

    @Test
    public void users() throws Exception {
        BaseConfig baseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
        try (DataBase dataBase = new DataBase(baseConfig)) {
            FrontendManagementInstaller installer = new FrontendManagementInstaller(dataBase);
            try {
                installer.install();
                Assert.assertEquals(1, Users.getOrCreateUserId(dataBase, "x@x.com"));
                Assert.assertEquals(1, Users.getOrCreateUserId(dataBase, "x@x.com"));
                Users.addKey(dataBase, 1, "key", new Date(System.currentTimeMillis() + 1000 * 60));
                Assert.assertEquals("key", Users.listKeys(dataBase, 1).get(0));
                Users.removeAllKeys(dataBase, 1);
                Assert.assertEquals(0, Users.listKeys(dataBase, 1).size());
            } finally {
                installer.uninstall();
            }
        }
    }

    @Test
    public void authorities() throws Exception {
        BaseConfig baseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
        try (DataBase dataBase = new DataBase(baseConfig)) {
            FrontendManagementInstaller installer = new FrontendManagementInstaller(dataBase);
            try {
                installer.install();
                int failures = 0;
                Assert.assertEquals(0, Authorities.list(dataBase, 1).size());
                Authorities.createAuthority(dataBase, 1, "auth_space_1");
                try {
                    Authorities.createAuthority(dataBase, 1, "auth_space_1");
                    Assert.fail();
                } catch (ErrorCodeException ece) {
                    failures++;
                    Assert.assertEquals(601088, ece.code);
                }
                {
                    ArrayList<String> listing = Authorities.list(dataBase, 1);
                    Assert.assertEquals(1, listing.size());
                    Assert.assertEquals("auth_space_1", listing.get(0));
                }

                Authorities.setKeystore(dataBase, 1, "auth_space_1", "{\"x\":1}");
                Assert.assertEquals("{\"x\":1}", Authorities.getKeystoreInternal(dataBase, "auth_space_1"));
                Authorities.deleteAuthority(dataBase, 1, "auth_space_1");
                Assert.assertEquals(0, Authorities.list(dataBase, 1).size());
                try {
                    Authorities.getKeystoreInternal(dataBase, "auth_space_1");
                    Assert.fail();
                } catch (ErrorCodeException ece) {
                    failures++;
                    Assert.assertEquals(643072, ece.code);
                }
                try {
                    Authorities.setKeystore(dataBase, 1, "auth_space_1", "{\"x\":1}");
                    Assert.fail();
                } catch (ErrorCodeException ece) {
                    failures++;
                    Assert.assertEquals(634880, ece.code);
                }
                try {
                    Authorities.deleteAuthority(dataBase, 1, "auth_space_1");
                    Assert.fail();
                } catch (ErrorCodeException ece) {
                    failures++;
                    Assert.assertEquals(654339, ece.code);
                }
                Authorities.createAuthority(dataBase, 1, "auth_space_1");
                Authorities.setKeystore(dataBase, 1, "auth_space_1", "{\"x\":2}");
                Assert.assertEquals("{\"x\":2}", Authorities.getKeystoreInternal(dataBase, "auth_space_1"));
                Authorities.setKeystore(dataBase, 1, "auth_space_1", "{\"x\":3}");
                Assert.assertEquals("{\"x\":3}", Authorities.getKeystoreInternal(dataBase, "auth_space_1"));
                {
                    ArrayList<String> listing = Authorities.list(dataBase, 1);
                    Assert.assertEquals(1, listing.size());
                    Assert.assertEquals("auth_space_1", listing.get(0));
                }
                Authorities.changeOwner(dataBase, "auth_space_1", 1, 2);
                Assert.assertEquals(0, Authorities.list(dataBase, 1).size());
                {
                    ArrayList<String> listing = Authorities.list(dataBase, 2);
                    Assert.assertEquals(1, listing.size());
                    Assert.assertEquals("auth_space_1", listing.get(0));
                }
                try {
                    Authorities.changeOwner(dataBase, "auth_space_1", 4, 3);
                    Assert.fail();
                } catch (ErrorCodeException ece) {
                    failures++;
                    Assert.assertEquals(662528, ece.code);
                }
                Assert.assertEquals(5, failures);

            } finally {
                installer.uninstall();
            }
        }
    }

    @Test
    public void spaces() throws Exception {
        BaseConfig baseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
        try (DataBase dataBase = new DataBase(baseConfig)) {
            FrontendManagementInstaller installer = new FrontendManagementInstaller(dataBase);
            try {
                installer.install();
                int alice = Users.getOrCreateUserId(dataBase, "alice@x.com");
                int bob = Users.getOrCreateUserId(dataBase, "bob@x.com");
                Assert.assertEquals(1, Spaces.createSpace(dataBase, alice, "space1"));
                Assert.assertEquals(1, Spaces.createSpace(dataBase, alice, "space1"));
                Assert.assertEquals(1, Spaces.getSpaceId(dataBase, "space1").id);
                Assert.assertEquals(2, Spaces.createSpace(dataBase, bob, "space2"));
                Assert.assertEquals(2, Spaces.createSpace(dataBase, bob, "space2"));
                try {
                    Spaces.createSpace(dataBase, bob, "space1");
                    Assert.fail();
                } catch (ErrorCodeException ex) {
                    Assert.assertEquals(679948, ex.code);
                }
                try {
                    Spaces.createSpace(dataBase, alice, "space2");
                    Assert.fail();
                } catch (ErrorCodeException ex) {
                    Assert.assertEquals(679948, ex.code);
                }
                Assert.assertEquals(2, Spaces.getSpaceId(dataBase, "space2").id);
                Assert.assertEquals("{}", Spaces.getPlan(dataBase, 1));
                Assert.assertEquals("{}", Spaces.getPlan(dataBase, 2));
                Spaces.setPlan(dataBase, 1, "{\"x\":1}");
                Spaces.setPlan(dataBase, 2, "{\"x\":2}");
                Spaces.setBilling(dataBase, 1, "fixed50");
                Spaces.setBilling(dataBase, 2, "open");
                Assert.assertEquals("{\"x\":1}", Spaces.getPlan(dataBase, 1));
                Assert.assertEquals("{\"x\":2}", Spaces.getPlan(dataBase, 2));
                {
                    List<Spaces.Item> ls1 = Spaces.list(dataBase, alice, null, 5);
                    List<Spaces.Item> ls2 = Spaces.list(dataBase, bob, null, 5);
                    Assert.assertEquals(1, ls1.size());
                    Assert.assertEquals(1, ls2.size());
                    Assert.assertEquals("space1", ls1.get(0).name);
                    Assert.assertEquals("space2", ls2.get(0).name);
                    Assert.assertEquals("owner", ls1.get(0).callerRole);
                    Assert.assertEquals("owner", ls2.get(0).callerRole);
                    Assert.assertEquals("fixed50", ls1.get(0).billing);
                    Assert.assertEquals("open", ls2.get(0).billing);
                }
                Spaces.setRole(dataBase, 2, alice, Role.Developer);
                {
                    Spaces.Space space1 = Spaces.getSpaceId(dataBase, "space1");
                    Spaces.Space space2 = Spaces.getSpaceId(dataBase, "space2");
                    Assert.assertTrue(space1.developers.contains(1));
                    Assert.assertTrue(space2.developers.contains(1));
                    Assert.assertTrue(space2.developers.contains(2));
                    List<Spaces.Item> ls1 = Spaces.list(dataBase, alice, null, 5);
                    List<Spaces.Item> ls2 = Spaces.list(dataBase, bob, null, 5);
                    Assert.assertEquals(2, ls1.size());
                    Assert.assertEquals(1, ls2.size());
                    Assert.assertEquals("space1", ls1.get(0).name);
                    Assert.assertEquals("space2", ls1.get(1).name);
                    Assert.assertEquals("space2", ls2.get(0).name);
                    Assert.assertEquals("owner", ls1.get(0).callerRole);
                    Assert.assertEquals("developer", ls1.get(1).callerRole);
                    Assert.assertEquals("owner", ls2.get(0).callerRole);
                }
                Spaces.changePrimaryOwner(dataBase, 1, alice, bob);
                {
                    List<Spaces.Item> ls1 = Spaces.list(dataBase, alice, null, 5);
                    List<Spaces.Item> ls2 = Spaces.list(dataBase, bob, null, 5);
                    Assert.assertEquals(1, ls1.size());
                    Assert.assertEquals(2, ls2.size());
                    Assert.assertEquals("space2", ls1.get(0).name);
                    Assert.assertEquals("space1", ls2.get(0).name);
                    Assert.assertEquals("space2", ls2.get(1).name);
                    Assert.assertEquals("developer", ls1.get(0).callerRole);
                    Assert.assertEquals("owner", ls2.get(0).callerRole);
                    Assert.assertEquals("owner", ls2.get(1).callerRole);
                }
                Spaces.setRole(dataBase, 2, alice, Role.None);
                {
                    List<Spaces.Item> ls1 = Spaces.list(dataBase, alice, null, 5);
                    List<Spaces.Item> ls2 = Spaces.list(dataBase, bob, null, 5);
                    Assert.assertEquals(0, ls1.size());
                    Assert.assertEquals(2, ls2.size());
                    Assert.assertEquals("space1", ls2.get(0).name);
                    Assert.assertEquals("space2", ls2.get(1).name);
                }

                try {
                    Spaces.createSpace(dataBase, alice, "space1");
                    Assert.fail();
                } catch (ErrorCodeException ex) {
                    Assert.assertEquals(679948, ex.code);
                }
                try {
                    Spaces.getSpaceId(dataBase, "space3");
                    Assert.fail();
                } catch (ErrorCodeException ex) {
                    Assert.assertEquals(625678, ex.code);
                }
                try {
                    Spaces.getPlan(dataBase, 5);
                    Assert.fail();
                } catch (ErrorCodeException ex) {
                    Assert.assertEquals(609294, ex.code);
                }
            } finally {
                installer.uninstall();
            }
        }
    }
}
