/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.extern.AssetUploader;
import org.adamalang.runtime.natives.NtAsset;

import java.io.File;

public class S3 implements AssetUploader {
  private final AmazonS3 s3;
  private final String bucket;

  public S3(AWSConfig config) {
    this.s3 = AmazonS3Client.builder().withRegion(config.region).withCredentials(config).build();
    this.bucket = config.bucketForAssets;
  }

  @Override
  public void upload(NtAsset asset, File localFile, Callback<Boolean> callback) {
    PutObjectRequest request = new PutObjectRequest(bucket, asset.id, localFile);
    try {
      s3.putObject(request);
    } catch (Exception ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.API_ASSET_UPLOAD_FAILED, ex));
    }
  }
}
