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
package org.adamalang.web.assets;

import java.util.Locale;
import java.util.TreeMap;

/** Get the content type of a file name */
public class ContentType {
  private static TreeMap<String, String> EXT_TO_CONTENT_TYPE = buildMap();

  public static TreeMap<String, String> buildMap() {
    TreeMap<String, String> map = new TreeMap<>();
    map.put("aac","audio/aac");
    map.put("abw","application/x-abiword");
    map.put("arc","application/x-freearc");
    map.put("avif","image/avif");
    map.put("avi","video/x-msvideo");
    map.put("azw","application/vnd.amazon.ebook");
    map.put("bin","application/octet-stream");
    map.put("bmp","image/bmp");
    map.put("bz","application/x-bzip");
    map.put("bz2","application/x-bzip2");
    map.put("cda","application/x-cdf");
    map.put("csh","application/x-csh");
    map.put("css","text/css");
    map.put("csv","text/csv");
    map.put("doc","application/msword");
    map.put("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    map.put("eot","application/vnd.ms-fontobject");
    map.put("epub","application/epub+zip");
    map.put("gz","application/gzip");
    map.put("gif","image/gif");
    map.put("htm","text/html");
    map.put("html","text/html");
    map.put("ico","image/vnd.microsoft.icon");
    map.put("ics","text/calendar");
    map.put("jar","application/java-archive");
    map.put("jpeg","image/jpeg");
    map.put("jpg","image/jpeg");
    map.put("js","text/javascript");
    map.put("json","application/json");
    map.put("jsonld","application/ld+json");
    map.put("mid","audio/midi");
    map.put("mjs","text/javascript");
    map.put("mp3","audio/mpeg");
    map.put("mp4","video/mp4");
    map.put("mpeg","video/mpeg");
    map.put("mpkg","application/vnd.apple.installer+xml");
    map.put("odp","application/vnd.oasis.opendocument.presentation");
    map.put("ods","application/vnd.oasis.opendocument.spreadsheet");
    map.put("odt","application/vnd.oasis.opendocument.text");
    map.put("oga","audio/ogg");
    map.put("ogv","video/ogg");
    map.put("ogx","application/ogg");
    map.put("opus","audio/opus");
    map.put("otf","font/otf");
    map.put("png","image/png");
    map.put("pdf","application/pdf");
    map.put("php","application/x-httpd-php");
    map.put("ppt","application/vnd.ms-powerpoint");
    map.put("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
    map.put("rar","application/vnd.rar");
    map.put("rtf","application/rtf");
    map.put("sh","application/x-sh");
    map.put("svg","image/svg+xml");
    map.put("swf","application/x-shockwave-flash");
    map.put("tar","application/x-tar");
    map.put("tif","image/tiff");
    map.put("tiff","image/tiff");
    map.put("ts","video/mp2t");
    map.put("ttf","font/ttf");
    map.put("txt","text/plain");
    map.put("vsd","application/vnd.visio");
    map.put("wasm", "application/wasm");
    map.put("wav","audio/wav");
    map.put("weba","audio/webm");
    map.put("webm","video/webm");
    map.put("webp","image/webp");
    map.put("woff","font/woff");
    map.put("woff2","font/woff2");
    map.put("xhtml","application/xhtml+xml");
    map.put("xls","application/vnd.ms-excel");
    map.put("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    map.put("xml","application/xml");
    map.put("xul","application/vnd.mozilla.xul+xml");
    map.put("zip","application/zip");
    map.put("3gp","video/3gpp");
    map.put("3g2","video/3gpp2");
    map.put("7z","application/x-7z-compressed");
    map.put("webmanifest", "application/manifest+json");
    return map;
  }

  public static String of(String filename) {
    String contentType = null;
    if (filename != null) {
      int lastDot = filename.lastIndexOf('.');
      if (lastDot >= 0) {
        String ext = filename.substring(lastDot + 1).toLowerCase(Locale.ENGLISH);
        contentType = EXT_TO_CONTENT_TYPE.get(ext);
      }
    }
    if (contentType == null) {
      switch (filename) {
        case ".gitignore":
          contentType = "text/plain";
          break;
      }
    }
    if (contentType == null) {
      contentType = "application/octet-stream";
    }
    return contentType;
  }
}
