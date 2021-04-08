package org.adamalang.runtime.contracts;

import java.io.InputStream;
import java.util.function.Supplier;

/** the inputs for the asset service to do its job */
public interface AssetRequest {
  /** the internal/given name of the asset */
  public String name();

  /** the space of the document */
  public String space();

  /** the id of the document */
  public long documentId();

  /** the content-type of the asset */
  public String type();

  /** the size of the asset in bytes */
  public long size();

  /** the md5 of the asset */
  public String md5();

  /** the sha384 of the asset */
  public String sha384();

  /** get the stream of the asset */
  public Supplier<InputStream> source();
}
