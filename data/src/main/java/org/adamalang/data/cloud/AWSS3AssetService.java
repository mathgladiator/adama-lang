/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.data.cloud;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.adamalang.runtime.stdlib.IdCodec;
import org.adamalang.mysql.IdFactory;
import org.adamalang.runtime.contracts.AssetRequest;
import org.adamalang.runtime.contracts.AssetService;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtAsset;

import java.util.HashMap;

public class AWSS3AssetService implements AssetService {
  private final AmazonS3 s3;
  private final String bucket;
  private final IdFactory idFactory;

  public AWSS3AssetService(AmazonS3 s3, String bucket, IdFactory idFactory) {
    this.s3 = s3;
    this.bucket = bucket;
    this.idFactory = idFactory;
  }

  @Override
  public void upload(AssetRequest request, Callback<NtAsset> callback) {
    idFactory.genId(new Callback<Long>() {
      @Override
      public void success(Long id) {
        try {
          ObjectMetadata metadata = new ObjectMetadata();
          metadata.setContentLength(request.size());
          metadata.setContentMD5(request.md5());
          metadata.setContentType(request.type());
          HashMap<String, String> userMetadata = new HashMap<>();
          userMetadata.put("sha384", request.sha384());
          userMetadata.put("documentId", "" + request.documentId());
          userMetadata.put("assetId", "" + id);
          metadata.setUserMetadata(userMetadata);
          // TODO: encode better; the document id AND id needs a bijective encoder such that the bits are reversed
          String key = "assets/" + IdCodec.encode(request.documentId()) + "/" + IdCodec.encode(id);
          s3.putObject(bucket, key, request.source().get(), metadata);
          NtAsset asset = new NtAsset("" + id, request.name(), request.type(), request.size(), request.md5(), request.sha384());
          callback.success(asset);
        } catch (Throwable t) {
          callback.failure(new ErrorCodeException(0, t));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
    // TODO: schedule on the right thread

  }
}
