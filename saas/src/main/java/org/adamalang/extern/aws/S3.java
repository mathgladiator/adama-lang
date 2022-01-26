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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.extern.AssetUploader;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

public class S3 implements AssetUploader {
  private static final Logger LOGGER = LoggerFactory.getLogger(S3.class);
  private final S3Client s3;
  private final AWSMetrics metrics;
  private final String bucket;

  public S3(AWSConfig config, AWSMetrics metrics) {
    this.s3 = S3Client.builder().region(Region.of(config.region)).credentialsProvider(config).build();
    this.metrics = metrics;
    this.bucket = config.bucketForAssets;
  }

  @Override
  public void upload(Key key, NtAsset asset, File localFile, Callback<Void> callback) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.upload_file.start();
    PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key.space + "/" + key.key + "/" + asset.id).build();
    try {
      s3.putObject(request, RequestBody.fromFile(localFile));
      instance.success();
      callback.success(null);
    } catch (Exception ex) {
      LOGGER.error("failed-upload-file", ex);
      instance.failure(ErrorCodes.API_ASSET_UPLOAD_FAILED);
      callback.failure(new ErrorCodeException(ErrorCodes.API_ASSET_UPLOAD_FAILED, ex));
    }
  }
}
