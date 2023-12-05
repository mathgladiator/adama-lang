/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.extern.aws;

import org.adamalang.ErrorCodes;
import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.common.*;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.runtime.data.ColdAssetSystem;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.PostDocumentDelete;
import org.adamalang.runtime.deploy.AsyncByteCodeCache;
import org.adamalang.runtime.deploy.CachedByteCode;
import org.adamalang.runtime.deploy.ExternalByteCodeSystem;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.AssetUploadBody;
import org.adamalang.web.client.*;
import org.adamalang.web.contracts.WellKnownHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class S3 implements Cloud, WellKnownHandler, PostDocumentDelete, ColdAssetSystem, ExternalByteCodeSystem {
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
    S3SimpleHttpRequestBuilder builder = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "PUT", s3key, null);
    builder.withContentType(asset.contentType);
    builder.withContentMD5(asset.md5);
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
    base.executeShared(request, new VoidCallbackHttpResponder(LOGGER, instance, callback));
  }

  public void request(AssetRequest asset, AssetStream stream) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.download_file.start();
    String s3key = "assets/" + asset.space + "/" + asset.key + "/" + asset.id;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "GET", s3key, null).buildWithEmptyBody();
    base.executeShared(request, new SimpleHttpResponder() {
      private String contentType;
      private String contentMd5;
      private long written;
      private long size;
      private boolean failed = false;
      private MessageDigest digest = Hashing.md5();

      @Override
      public void start(SimpleHttpResponseHeader header) {
        if (header.status == 200) {
          this.contentType = header.headers.get("content-type");
          this.contentMd5 = header.headers.get("content-md5");
          if (contentMd5 == null) {
            this.contentMd5 = header.headers.get("x-amz-meta-md5");
          }
          this.written = 0;
        } else {
          failed = true;
          LOGGER.error("failed-request: {} -> {}", header.status, header.headers.toString());
          stream.failure(ErrorCodes.STREAM_ASSET_NOT_200);
          instance.failure(ErrorCodes.STREAM_ASSET_NOT_200);
        }
      }

      @Override
      public void bodyStart(long size) {
        this.size = size;
        stream.headers(size, this.contentType, this.contentMd5);
      }

      @Override
      public void bodyFragment(byte[] chunk, int offset, int len) {
        written += len;
        boolean last = written == size;
        digest.update(chunk, offset, len);
        if (last && this.contentMd5 != null) {
          String check = Hashing.finishAndEncode(digest);
          if (!check.equals(this.contentMd5)) {
            stream.failure(ErrorCodes.STREAM_ASSET_CORRUPTED);
            return;
          }
        }
        stream.body(chunk, offset, len, last);
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
        SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.logBucket, "PUT", s3key, null).buildWithFileAsBody(new FileReaderHttpRequestBody(file));
        final File fileToDeleteOnSuccess = file;
        base.executeShared(request, new VoidCallbackHttpResponder(LOGGER, metrics.upload_log_document.start(), new Callback<Void>() {
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
  public void exists(Key key, String archiveKey, Callback<Void> callback) {
    String s3key = "backups/" + key.space + "/" + key.key + "/#" + archiveKey;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "HEAD", s3key, null).buildWithEmptyBody();
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.exists_document.start();
    base.executeShared(request, new VoidCallbackHttpResponder(LOGGER, instance, callback));
  }

  @Override
  public void restore(Key key, String archiveKey, Callback<File> callback) {
    File root = new File(path(), key.space);
    if (!root.exists()) {
      root.mkdir();
    }
    File temp = new File(root, archiveKey + ".temp");
    String s3key = "backups/" + key.space + "/" + key.key + "/#" + archiveKey;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "GET", s3key, null).buildWithEmptyBody();
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.restore_document.start();
    try {
      base.executeShared(request, new FileWriterHttpResponder(temp, metrics.alarm_file_not_found, new Callback<Void>() {
        @Override
        public void success(Void value) {
          File dest = new File(root, archiveKey);
          try {
            Files.move(temp.toPath(), dest.toPath(), StandardCopyOption.ATOMIC_MOVE);
            instance.success();
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
      SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "PUT", s3key, null).buildWithFileAsBody(body);
      base.executeShared(request, new VoidCallbackHttpResponder(LOGGER, metrics.backup_document.start(), callback));
    } catch (Exception ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.BACKUP_FILE_FAILURE, ex));
    }
  }

  @Override
  public void delete(Key key, String archiveKey, Callback<Void> callback) {
    String s3key = "backups/" + key.space + "/" + key.key + "/#" + archiveKey;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "DELETE", s3key, null).buildWithEmptyBody();
    base.executeShared(request, new VoidCallbackHttpResponder(LOGGER, metrics.delete_document.start(), callback));
  }

  @Override
  public void handle(String uri, Callback<String> callback) {
    String s3key = "wellknown" + uri;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "GET", s3key, null).buildWithEmptyBody();
    base.executeShared(request, new StringCallbackHttpResponder(LOGGER, metrics.well_known_get.start(), callback));
  }

  @Override
  public void listAssetsOf(Key key, Callback<List<String>> callback) {
    final ArrayList<String> ids = new ArrayList<>();
    final TreeMap<String, String> parameters = new TreeMap<>();
    String prefix = "assets/" + key.space + "/" + key.key + "/";
    parameters.put("prefix", prefix);
    base.executeShared(new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "GET", "", parameters).buildWithEmptyBody(), new StringCallbackHttpResponder(LOGGER, metrics.list_assets.start(), new Callback<String>() {
      @Override
      public void success(String xml) {
        try {
          S3XmlParsing.ListResult results = S3XmlParsing.listResultOf(xml);
          for (String key : results.keys) {
            ids.add(key.substring(prefix.length()));
          }
          if (results.truncated) {
            parameters.put("marker", results.last());
            base.executeShared(new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "GET", "", parameters).buildWithEmptyBody(), new StringCallbackHttpResponder(LOGGER, metrics.list_assets.start(), this));
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
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "DELETE", s3key, null).buildWithEmptyBody();
    base.executeShared(request, new VoidCallbackHttpResponder(LOGGER, metrics.delete_asset.start(), callback));
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

  private void attemptFetchByteCode(String className, int retriesLeft, int backoff, Callback<CachedByteCode> callback) {
    String s3key = "bytecode/" + className.replaceAll(Pattern.quote("_"), Matcher.quoteReplacement("/")) + "/" + Platform.VERSION;
    SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "GET", s3key, null).buildWithEmptyBody();
    base.executeShared(request, new ByteArrayCallbackHttpResponder(LOGGER, metrics.fetch_byte_code.start(), new Callback<byte[]>() {
      @Override
      public void success(byte[] value) {
        try {
          callback.success(CachedByteCode.unpack(value));
        } catch (ErrorCodeException ex) {
          failure(ex);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        if (retriesLeft > 0 && ex.code != ErrorCodes.WEB_CALLBACK_RESOURCE_NOT_FOUND) {
          metrics.retry_fetch_byte_code.run();
          base.executor.schedule(new NamedRunnable("attempt-fetch-bytecode-retry") {
            @Override
            public void execute() throws Exception {
              attemptFetchByteCode(className, retriesLeft - 1, (int) (backoff * (1 + Math.random())),  callback);
            }
          }, (int) (Math.random() + 1000 + backoff));
        } else {
          callback.failure(ex);
        }
      }
    }));
  }

  @Override
  public void fetchByteCode(String className, Callback<CachedByteCode> callback) {
    attemptFetchByteCode(className, 4, 250, callback);
  }

  @Override
  public void storeByteCode(String className, CachedByteCode code, Callback<Void> callback) {
    try {
      String s3key = "bytecode/" + className.replaceAll(Pattern.quote("_"), Matcher.quoteReplacement("/")) + "/" + Platform.VERSION;
      SimpleHttpRequest request = new S3SimpleHttpRequestBuilder(config, config.userDataBucket, "PUT", s3key, null).buildWithBytesAsBody(code.pack());
      base.executeShared(request, new VoidCallbackHttpResponder(LOGGER, metrics.store_byte_code.start(), callback));
    } catch (ErrorCodeException ex) {
      callback.failure(ex);
    }
  }
}
