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
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.runtime.data.ColdAssetSystem;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.PostDocumentDelete;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.AssetUploadBody;
import org.adamalang.web.client.*;
import org.adamalang.web.contracts.WellKnownHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class S3 implements Cloud, WellKnownHandler, PostDocumentDelete, ColdAssetSystem {
  private static final Logger LOGGER = LoggerFactory.getLogger(S3.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);
  private static final Pattern COMPLETE_LOG = Pattern.compile("[a-z]*\\.[0-9]*-[0-9]*-[0-9]*\\.[0-9]*\\.log");
  private final WebClientBase base;
  private final AWSMetrics metrics;
  private final AWSConfig config;
  private final File archive;

  public S3(WebClientBase base, AWSConfig config, AWSMetrics metrics) {
    this.base = base;
    this.config = config;
    this.metrics = metrics;
    this.archive = new File(config.archivePath);
    if (!archive.exists()) {
      archive.mkdirs();
    }
    if (!archive.exists() || !archive.isDirectory()) {
      throw new RuntimeException("archive '" + config.archivePath + "' is no a valid directory");
    }
  }

  public void upload(Key key, NtAsset asset, AssetUploadBody body, Callback<Void> callback) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.upload_file.start();
    String s3key = "assets/" + key.space + "/" + key.key + "/" + asset.id;
    S3SimpleHttpRequestBuilder builder = new S3SimpleHttpRequestBuilder(config, "PUT", s3key, null);
    builder.withContentType(asset.contentType);
    final SimpleHttpRequest request;
    if (body.getFileIfExists() != null) {
      try {
        request = builder.buildWithFileAsBody(new FileReaderHttpRequestBody(body.getFileIfExists()));
      } catch (Exception ex) {
        callback.failure(new ErrorCodeException(ErrorCodes.UPLOAD_SCAN_FILE_FAILURE, ex));
        return;
      }
    } else {
      request = builder.buildWithBytesAsBody(body.getBytes());
    }
    base.execute(request, new VoidCallbackHttpResponder(LOGGER, instance, callback));
  }

  public void request(AssetRequest asset, AssetStream stream) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.download_file.start();
    String s3key = "assets/" + asset.space + "/" + asset.key + "/" + asset.id;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, "GET", s3key, null).buildWithEmptyBody();
    base.executeShared(request, new SimpleHttpResponder() {
      private String contentType;
      private long written;
      private long size;
      private boolean failed = false;

      @Override
      public void start(SimpleHttpResponseHeader header) {
        if (header.status == 200) {
          this.contentType = header.headers.get("content-type");
          this.written = 0;
        } else {
          failed = true;
          stream.failure(ErrorCodes.STREAM_ASSET_NOT_200);
          instance.failure(ErrorCodes.STREAM_ASSET_NOT_200);
        }
      }

      @Override
      public void bodyStart(long size) {
        this.size = size;
        stream.headers(size, this.contentType);
      }

      @Override
      public void bodyFragment(byte[] chunk, int offset, int len) {
        written += len;
        stream.body(chunk, offset, len, written == size);
      }

      @Override
      public void bodyEnd() {
        instance.success();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOGGER.error("failed-request-asset:", ex);
        if (!failed) {
          failed = true;
          stream.failure(ex.code);
        }
      }
    });
  }

  @Override
  public File path() {
    return archive;
  }

  public static boolean shouldConsiderForUpload(String name) {
    return COMPLETE_LOG.matcher(name).matches();
  }

  public void uploadLogs(File directory, String prefix) throws Exception {
    for (File file : directory.listFiles()) {
      if (shouldConsiderForUpload(file.getName())) {
        String s3key = "logs/" + prefix + "/" + file.getName();
        SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, "PUT", s3key, null).buildWithFileAsBody(new FileReaderHttpRequestBody(file));
        final File fileToDeleteOnSuccess = file;
        base.execute(request, new VoidCallbackHttpResponder(LOGGER, metrics.upload_log_document.start(), new Callback<Void>() {
          @Override
          public void success(Void value) {
            fileToDeleteOnSuccess.delete();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        }));
      }
    }
  }

  @Override
  public void restore(Key key, String archiveKey, Callback<File> callback) {
    File root = new File(path(), key.space);
    if (!root.exists()) {
      root.mkdir();
    }
    File temp = new File(root, archiveKey + ".temp");
    String s3key = "backups/" + key.space + "/" + key.key + "/#" + archiveKey;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, "GET", s3key, null).buildWithEmptyBody();
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.restore_document.start();
    try {
      base.execute(request, new FileWriterHttpResponder(temp, new Callback<Void>() {
        @Override
        public void success(Void value) {
          File dest = new File(root, archiveKey);
          try {
            Files.move(temp.toPath(), dest.toPath(), StandardCopyOption.ATOMIC_MOVE);
            callback.success(dest);
          } catch (Exception ex) {
            S3.LOGGER.error("failed-restore-file", ex);
            instance.failure(ErrorCodes.API_CLOUD_RESTORE_FAILED);
            callback.failure(new ErrorCodeException(ErrorCodes.API_CLOUD_RESTORE_FAILED, ex));
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          instance.failure(ex.code);
          callback.failure(ex);
        }
      }));
    } catch (ErrorCodeException ex) {
      callback.failure(ex);
    }
  }

  @Override
  public void backup(Key key, File archiveFile, Callback<Void> callback) {
    try {
      String s3key = "backups/" + key.space + "/" + key.key + "/#" + archiveFile.getName();
      FileReaderHttpRequestBody body = new FileReaderHttpRequestBody(archiveFile);
      SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, "PUT", s3key, null).buildWithFileAsBody(body);
      base.execute(request, new VoidCallbackHttpResponder(LOGGER, metrics.backup_document.start(), callback));
    } catch (Exception ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.BACKUP_FILE_FAILURE, ex));
    }
  }

  @Override
  public void delete(Key key, String archiveKey, Callback<Void> callback) {
    String s3key = "backups/" + key.space + "/" + key.key + "/#" + archiveKey;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, "DELETE", s3key, null).buildWithEmptyBody();
    base.execute(request, new VoidCallbackHttpResponder(LOGGER, metrics.delete_document.start(), callback));
  }

  @Override
  public void handle(String uri, Callback<String> callback) {
    String s3key = "wellknown" + uri;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, "GET", s3key, null).buildWithEmptyBody();
    base.execute(request, new StringCallbackHttpResponder(LOGGER, metrics.well_known_get.start(), callback));
  }

  @Override
  public void listAssetsOf(Key key, Callback<List<String>> callback) {
    final ArrayList<String> ids = new ArrayList<>();
    final TreeMap<String, String> parameters = new TreeMap<>();
    String prefix = "assets/" + key.space + "/" + key.key + "/";
    parameters.put("prefix", prefix);
    base.execute(new S3SimpleHttpRequestBuilder(config, "GET", "", parameters).buildWithEmptyBody(), new StringCallbackHttpResponder(LOGGER, metrics.list_assets.start(), new Callback<String>() {
      @Override
      public void success(String xml) {
        try {
          S3XmlParsing.ListResult results = S3XmlParsing.listResultOf(xml);
          for (String key : results.keys) {
            ids.add(key.substring(prefix.length()));
          }
          if (results.truncated) {
            parameters.put("marker", results.last());
            base.execute(new S3SimpleHttpRequestBuilder(config, "GET", "", parameters).buildWithEmptyBody(), new StringCallbackHttpResponder(LOGGER, metrics.list_assets.start(), this));
          } else {
            callback.success(ids);
          }
        } catch (Exception ex) {
          callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.LIST_ASSETS_PARSE_FAILURE, ex, EXLOGGER));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    }));
  }

  @Override
  public void deleteAsset(Key key, String assetId, Callback<Void> callback) {
    String s3key = "assets/" + key.space + "/" + key.key + "/" + assetId;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, "DELETE", s3key, null).buildWithEmptyBody();
    base.execute(request, new VoidCallbackHttpResponder(LOGGER, metrics.delete_asset.start(), callback));
  }

  @Override
  public void deleteAllAssets(Key key, Callback<Void> callback) {
    listAssetsOf(key, new Callback<>() {
      @Override
      public void success(List<String> ids) {
        for (String id : ids) {
          deleteAsset(key, id, Callback.DONT_CARE_VOID);
        }
        callback.success(null);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }
}
