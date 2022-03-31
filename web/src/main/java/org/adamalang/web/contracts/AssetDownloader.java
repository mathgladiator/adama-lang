package org.adamalang.web.contracts;

import org.adamalang.web.service.AssetRequest;

public interface AssetDownloader {

  public static interface AssetStream {
    public void headers(String contentType);

    public void body(byte[] chunk, int offset, int length, boolean last);

    public void failure(int code);
  }

  public void request(AssetRequest request, AssetStream stream);


}
