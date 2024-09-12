/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.sys.readonly;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.ServiceShield;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.MockReplicationInitiator;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class ReadOnlyServiceTests {
  @Test
  public void shed_flow() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(ReadOnlyReplicaThreadBaseTests.SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyService readonly = new ReadOnlyService(ReadOnlyReplicaThreadBaseTests.METRICS, new ServiceShield(), factoryFactory, seed, new MockTime(), 3);
    try {
      MockReadOnlyStream stream = new MockReadOnlyStream();
      Runnable latched1 = stream.latchAt(3);
      Runnable latched2 = stream.latchAt(4);
      Runnable latched3 = stream.latchAt(5);
      readonly.obverse(ContextSupport.WRAP(ReadOnlyReplicaThreadBaseTests.WHO), ReadOnlyReplicaThreadBaseTests.KEY,"{\"echo\":420}", stream);
      stream.await_began();
      latched1.run();
      Assert.assertEquals("{\"view-state-filter\":[\"echo\"]}", stream.get(0));
      Assert.assertEquals("{\"data\":{\"x\":123,\"foo\":420},\"seq\":0}", stream.get(1));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":0}", stream.get(2));
      readonly.shed((key) -> false);
      stream.get().update("{\"echo\":111}");
      latched2.run();
      Assert.assertEquals("{\"data\":{\"foo\":111},\"seq\":0}", stream.get(3));
      readonly.shed((key) -> true);
      latched3.run();
      Assert.assertEquals("CLOSED", stream.get(4));
    } finally {
      readonly.shutdown();
    }
  }

  @Test
  public void deployment_flow() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(ReadOnlyReplicaThreadBaseTests.SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyService readonly = new ReadOnlyService(ReadOnlyReplicaThreadBaseTests.METRICS, new ServiceShield(), factoryFactory, seed, new MockTime(), 3);
    try {
      MockReadOnlyStream stream = new MockReadOnlyStream();
      Runnable latched1 = stream.latchAt(3);
      Runnable latched2 = stream.latchAt(4);
      Runnable latched3 = stream.latchAt(5);
      readonly.obverse(ContextSupport.WRAP(ReadOnlyReplicaThreadBaseTests.WHO), ReadOnlyReplicaThreadBaseTests.KEY,"{\"echo\":420}", stream);
      stream.await_began();
      latched1.run();
      Assert.assertEquals("{\"data\":{\"x\":123,\"foo\":420},\"seq\":0}", stream.get(1));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":0}", stream.get(2));
      readonly.shed((key) -> false);
      stream.get().update("{\"echo\":111}");
      latched2.run();
      Assert.assertEquals("{\"data\":{\"foo\":111},\"seq\":0}", stream.get(3));
      factoryFactory.set(LivingDocumentTests.compile(ReadOnlyReplicaThreadBaseTests.SIMPLE_CODE_DEPLOYED, Deliverer.FAILURE));
      readonly.deploy(new DeploymentMonitor() {
        @Override
        public void bumpDocument(boolean changed) {

        }

        @Override
        public void witnessException(ErrorCodeException ex) {

        }

        @Override
        public void finished(int ms) {

        }
      });
      latched3.run();
      Assert.assertEquals("CLOSED", stream.get(4));
    } finally {
      readonly.shutdown();
    }
  }
}
