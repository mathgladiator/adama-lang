package org.adamalang.runtime.data;

/** write an archive */
public interface ArchiveWriter {
  /** record a patch */
  public void record(RemoteDocumentUpdate patch);

  /** record a snapshot */
  public void snapshot(int seq, String document, int history);

  /** finish recording */
  public void finish();

  public void failed(int errorCode);
}
