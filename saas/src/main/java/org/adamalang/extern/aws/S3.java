/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.aws;

import org.adamalang.ErrorCodes;
import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedThreadFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.extern.AssetUploader;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.contracts.AssetDownloader;
import org.adamalang.web.service.AssetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class S3 implements AssetUploader, AssetDownloader, Cloud {
  private ExecutorService executors;
  private static final Logger LOGGER = LoggerFactory.getLogger(S3.class);
  private final S3Client s3;
  private final AWSMetrics metrics;
  private final String bucket;
  private final File archive;

  public S3(AWSConfig config, AWSMetrics metrics) {
    executors = Executors.newCachedThreadPool(new NamedThreadFactory("s3"));
    this.s3 = S3Client.builder().region(Region.of(config.region)).credentialsProvider(config).build();
    this.metrics = metrics;
    this.bucket = config.bucket;
    this.archive = new File(config.archivePath);
    if (!archive.exists()) {
      archive.mkdirs();
    }
    if (!archive.exists() || !archive.isDirectory()) {
      throw new RuntimeException("archive '" + config.archivePath + "' is no a valid directory");
    }
  }

  @Override
  public void upload(Key key, NtAsset asset, File localFile, Callback<Void> callback) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.upload_file.start();
    PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key("assets/" + key.space + "/" + key.key + "/" + asset.id).contentType(asset.contentType).build();
    executors.execute(() -> {
      try {
        s3.putObject(request, RequestBody.fromFile(localFile));
        instance.success();
        callback.success(null);
      } catch (Exception ex) {
        LOGGER.error("failed-upload-file", ex);
        instance.failure(ErrorCodes.API_ASSET_UPLOAD_FAILED);
        callback.failure(new ErrorCodeException(ErrorCodes.API_ASSET_UPLOAD_FAILED, ex));
      }
    });
  }

  @Override
  public void request(AssetRequest asset, AssetStream stream) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.download_file.start();
    executors.execute(() -> {
      try {
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key("assets/" + asset.space + "/" + asset.key + "/" + asset.id).build();
        ResponseInputStream<GetObjectResponse> response = s3.getObject(request);
        stream.headers(response.response().contentLength(), response.response().contentType());
        byte[] chunk = new byte[64 * 1024];
        long left = response.response().contentLength();
        int rd;
        while ((rd = response.read(chunk, 0, (int) Math.min(chunk.length, left))) >= 0 && left > 0) {
          left -= rd;
          stream.body(chunk, 0, rd, left == 0);
        }
        instance.success();
      } catch (Exception ex) {
        LOGGER.error("failed-download-file", ex);
        instance.failure(ErrorCodes.API_ASSET_DOWNLOAD_FAILED);
        stream.failure(ErrorCodes.API_ASSET_DOWNLOAD_FAILED);
      }
    });
  }

  @Override
  public File path() {
    return archive;
  }

  @Override
  public void restore(Key key, String archiveKey, Callback<File> callback) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.restore_document.start();
    executors.execute(() -> {
      try {
        File root = new File(path(), key.space);
        if (!root.exists()) {
          root.mkdir();
        }
        File file = new File(root, archiveKey);
        if (!file.exists()) {
          GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key("backups/" + key.space + "/" + key.key + "/#" + archiveKey).build();
          s3.getObject(request, file.toPath());
        }
        instance.success();
        callback.success(file);
      } catch (Exception ex) {
        LOGGER.error("failed-restore-file", ex);
        instance.failure(ErrorCodes.API_CLOUD_RESTORE_FAILED);
        callback.failure(new ErrorCodeException(ErrorCodes.API_CLOUD_RESTORE_FAILED, ex));
      }
    });
  }

  @Override
  public void backup(Key key, File archiveFile, Callback<Void> callback) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.backup_document.start();
    PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key("backups/" + key.space + "/" + key.key + "/#" + archiveFile.getName()).build();
    executors.execute(() -> {
      try {
        s3.putObject(request, archiveFile.toPath());
        instance.success();
        callback.success(null);
      } catch (Exception ex) {
        LOGGER.error("failed-backup-file", ex);
        instance.failure(ErrorCodes.API_CLOUD_BACKUP_FAILED);
        callback.failure(new ErrorCodeException(ErrorCodes.API_CLOUD_BACKUP_FAILED, ex));
      }
    });
  }

  @Override
  public void delete(Key key, String archiveKey) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.delete_document.start();
    DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucket).key("backups/" + key.space + "/" + key.key + "/#" + archiveKey).build();
    executors.execute(() -> {
      try {
        s3.deleteObject(request);
        instance.success();
      } catch (Exception ex) {
        LOGGER.error("failed-backup-file", ex);
        instance.failure(ErrorCodes.API_CLOUD_DELETE_FAILED);
      }
    });
  }

  private static Pattern COMPLETE_LOG = Pattern.compile("[a-z]*\\.[0-9]*-[0-9]*-[0-9]*\\.[0-9]*\\.log");

  public static boolean shouldConsiderForUpload(String name) {
    return COMPLETE_LOG.matcher(name).matches();
  }

  public void uploadLogs(File directory, String prefix) throws Exception {
    for (File file : directory.listFiles()) {
      if (shouldConsiderForUpload(file.getName())) {
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key("logs/" + prefix + "/" + file.getName()).build();
        s3.putObject(request, file.toPath());
        file.delete();
      }
    }
  }
}
