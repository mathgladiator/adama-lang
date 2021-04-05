package org.adamalang.runtime.contracts;

import org.junit.Test;

public class PerspectiveTests {
  @Test
  public void coverage() {
    Perspective.DEAD.data(null);
    Perspective.DEAD.disconnect();
  }
}
