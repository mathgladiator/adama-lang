/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

public class ErrorCodes {
  public static final int USERLAND_REQUEST_HAS_NO_CONNECTION = 40000;
  public static final int E5_UNCAUGHT_EXCEPTION_WEB_SOCKET = 5501;

  public static final int USERLAND_REQUEST_NO_GAMESPACE_PROPERTY = 40100;
  public static final int USERLAND_REQUEST_NO_GAME_PROPERTY = 40101;
  public static final int USERLAND_REQUEST_NO_METHOD_PROPERTY = 40102;
  public static final int USERLAND_REQUEST_INVALID_METHOD_PROPERTY = 40103;
  public static final int USERLAND_REQUEST_NO_CHANNEL_PROPERTY = 40104;
  public static final int USERLAND_REQUEST_NO_MESSAGE_PROPERTY = 40105;
  public static final int USERLAND_REQUEST_NO_CONSTRUCTOR_ARG = 40106;
  public static final int USERLAND_REQUEST_NO_MARKER_PROPERTY = 40107;
  public static final int USERLAND_REQUEST_NO_STREAM_PROPERTY = 40109;
  public static final int USERLAND_REQUEST_NO_ID_PROPERTY = 40110;

  public static final int USERLAND_REQUEST_IMPERSONATE_NO_AGENT = 40111;
  public static final int USERLAND_REQUEST_IMPERSONATE_NO_AUTHORITY = 40112;
}
