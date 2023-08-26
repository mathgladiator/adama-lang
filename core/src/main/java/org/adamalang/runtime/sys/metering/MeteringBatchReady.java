package org.adamalang.runtime.sys.metering;

public interface MeteringBatchReady {
  public void init(DiskMeteringBatchMaker me);

  public void ready(String batchId);
}
