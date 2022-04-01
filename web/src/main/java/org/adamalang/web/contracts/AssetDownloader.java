package org.adamalang.web.contracts;

import org.adamalang.web.service.AssetRequest;

public interface AssetDownloader {

  void request(AssetRequest request, AssetStream stream);

  interface AssetStream {
    void headers(long length, String contentType);

    void body(byte[] chunk, int offset, int length, boolean last);

    void failure(int code);
  }


}
