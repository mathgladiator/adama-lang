/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.assets;

import org.junit.Assert;
import org.junit.Test;

public class ContentTypeTests {
  @Test
  public void battery_a() {
    Assert.assertEquals("audio/aac", ContentType.of("filename.blah.aac"));
    Assert.assertEquals("application/x-abiword", ContentType.of("filename.blah.abw"));
    Assert.assertEquals("application/x-freearc", ContentType.of("filename.blah.arc"));
    Assert.assertEquals("image/avif", ContentType.of("filename.blah.avif"));
  }
  @Test
  public void battery_b() {
    Assert.assertEquals("video/x-msvideo", ContentType.of("filename.blah.avi"));
    Assert.assertEquals("application/vnd.amazon.ebook", ContentType.of("filename.blah.azw"));
    Assert.assertEquals("application/octet-stream", ContentType.of("filename.blah.bin"));
    Assert.assertEquals("image/bmp", ContentType.of("filename.blah.bmp"));
    Assert.assertEquals("application/x-bzip", ContentType.of("filename.blah.bz"));
    Assert.assertEquals("application/x-bzip2", ContentType.of("filename.blah.bz2"));
  }
  @Test
  public void battery_c() {
    Assert.assertEquals("application/x-cdf", ContentType.of("filename.blah.cda"));
    Assert.assertEquals("application/x-csh", ContentType.of("filename.blah.csh"));
    Assert.assertEquals("text/css", ContentType.of("filename.blah.css"));
    Assert.assertEquals("text/csv", ContentType.of("filename.blah.csv"));
  }
  @Test
  public void battery_d() {
    Assert.assertEquals("application/msword", ContentType.of("filename.blah.doc"));
    Assert.assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ContentType.of("filename.blah.docx"));
  }
  @Test
  public void battery_e() {
    Assert.assertEquals("application/vnd.ms-fontobject", ContentType.of("filename.blah.eot"));
    Assert.assertEquals("application/epub+zip", ContentType.of("filename.blah.epub"));
    Assert.assertEquals("application/gzip", ContentType.of("filename.blah.gz"));
  }
  @Test
  public void battery_g() {
    Assert.assertEquals("image/gif", ContentType.of("filename.blah.gif"));
  }
  @Test
  public void battery_h() {
    Assert.assertEquals("text/html", ContentType.of("filename.blah.htm"));
    Assert.assertEquals("text/html", ContentType.of("filename.blah.html"));
  }
  @Test
  public void battery_i() {
    Assert.assertEquals("image/vnd.microsoft.icon", ContentType.of("filename.blah.ico"));
    Assert.assertEquals("text/calendar", ContentType.of("filename.blah.ics"));
  }
  @Test
  public void battery_j() {
    Assert.assertEquals("application/java-archive", ContentType.of("filename.blah.jar"));
    Assert.assertEquals("image/jpeg", ContentType.of("filename.blah.jpeg"));
    Assert.assertEquals("text/javascript", ContentType.of("filename.blah.js"));
    Assert.assertEquals("application/json", ContentType.of("filename.blah.json"));
    Assert.assertEquals("application/ld+json", ContentType.of("filename.blah.jsonld"));
  }
  @Test
  public void battery_m() {
    Assert.assertEquals("audio/midi", ContentType.of("filename.blah.mid"));
    Assert.assertEquals("text/javascript", ContentType.of("filename.blah.mjs"));
    Assert.assertEquals("audio/mpeg", ContentType.of("filename.blah.mp3"));
    Assert.assertEquals("video/mp4", ContentType.of("filename.blah.mp4"));
    Assert.assertEquals("video/mpeg", ContentType.of("filename.blah.mpeg"));
    Assert.assertEquals("application/vnd.apple.installer+xml", ContentType.of("filename.blah.mpkg"));
  }
  @Test
  public void battery_o() {
    Assert.assertEquals("application/vnd.oasis.opendocument.presentation", ContentType.of("filename.blah.odp"));
    Assert.assertEquals("application/vnd.oasis.opendocument.spreadsheet", ContentType.of("filename.blah.ods"));
    Assert.assertEquals("application/vnd.oasis.opendocument.text", ContentType.of("filename.blah.odt"));
    Assert.assertEquals("audio/ogg", ContentType.of("filename.blah.oga"));
    Assert.assertEquals("video/ogg", ContentType.of("filename.blah.ogv"));
    Assert.assertEquals("application/ogg", ContentType.of("filename.blah.ogx"));
    Assert.assertEquals("audio/opus", ContentType.of("filename.blah.opus"));
    Assert.assertEquals("font/otf", ContentType.of("filename.blah.otf"));
  }
  @Test
  public void battery_p() {
    Assert.assertEquals("image/png", ContentType.of("filename.blah.png"));
    Assert.assertEquals("application/pdf", ContentType.of("filename.blah.pdf"));
    Assert.assertEquals("application/x-httpd-php", ContentType.of("filename.blah.php"));
    Assert.assertEquals("application/vnd.ms-powerpoint", ContentType.of("filename.blah.ppt"));
    Assert.assertEquals("application/vnd.openxmlformats-officedocument.presentationml.presentation", ContentType.of("filename.blah.pptx"));
  }
  @Test
  public void battery_r() {
    Assert.assertEquals("application/vnd.rar", ContentType.of("filename.blah.rar"));
    Assert.assertEquals("application/rtf", ContentType.of("filename.blah.rtf"));
  }
  @Test
  public void battery_s() {
    Assert.assertEquals("application/x-sh", ContentType.of("filename.blah.sh"));
    Assert.assertEquals("image/svg+xml", ContentType.of("filename.blah.svg"));
    Assert.assertEquals("application/x-shockwave-flash", ContentType.of("filename.blah.swf"));
  }
  @Test
  public void battery_t() {
    Assert.assertEquals("application/x-tar", ContentType.of("filename.blah.tar"));
    Assert.assertEquals("image/tiff", ContentType.of("filename.blah.tif"));
    Assert.assertEquals("image/tiff", ContentType.of("filename.blah.tiff"));
    Assert.assertEquals("video/mp2t", ContentType.of("filename.blah.ts"));
    Assert.assertEquals("font/ttf", ContentType.of("filename.blah.ttf"));
    Assert.assertEquals("text/plain", ContentType.of("filename.blah.txt"));
  }
  @Test
  public void battery_v() {
    Assert.assertEquals("application/vnd.visio", ContentType.of("filename.blah.vsd"));
  }
  @Test
  public void battery_w() {
    Assert.assertEquals("audio/wav", ContentType.of("filename.blah.wav"));
    Assert.assertEquals("audio/webm", ContentType.of("filename.blah.weba"));
    Assert.assertEquals("video/webm", ContentType.of("filename.blah.webm"));
    Assert.assertEquals("image/webp", ContentType.of("filename.blah.webp"));
    Assert.assertEquals("font/woff", ContentType.of("filename.blah.woff"));
    Assert.assertEquals("font/woff2", ContentType.of("filename.blah.woff2"));
  }
  @Test
  public void battery_x() {
    Assert.assertEquals("application/xhtml+xml", ContentType.of("filename.blah.xhtml"));
    Assert.assertEquals("application/vnd.ms-excel", ContentType.of("filename.blah.xls"));
    Assert.assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ContentType.of("filename.blah.xlsx"));
    Assert.assertEquals("application/xml", ContentType.of("filename.blah.xml"));
    Assert.assertEquals("application/vnd.mozilla.xul+xml", ContentType.of("filename.blah.xul"));
  }
  @Test
  public void battery_z() {
    Assert.assertEquals("application/zip", ContentType.of("filename.blah.zip"));
  }
  @Test
  public void battery_0123456789() {
    Assert.assertEquals("video/3gpp", ContentType.of("filename.blah.3gp"));
    Assert.assertEquals("video/3gpp2", ContentType.of("filename.blah.3g2"));
    Assert.assertEquals("application/x-7z-compressed", ContentType.of("filename.blah.7z"));
  }
  @Test
  public void not_found() {
    Assert.assertNull(ContentType.of("bad-filename"));
    Assert.assertNull(ContentType.of("bad-filename.noext"));
  }
}
