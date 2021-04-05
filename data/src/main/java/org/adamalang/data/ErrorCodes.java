/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.data;

/** error codes for things that go bump within the data package */
public class ErrorCodes {
  public static final int E4_FS_DATASERVICE_UNABLE_CREATE_LOG = 4001;
  public static final int E4_FS_DATASERVICE_CRASHED_CREATE_LOG = 4002;
  public static final int E4_FS_DATASERVICE_FILE_NOT_FOUND = 4005;
  public static final int E4_FS_DATASERVICE_CRASHED_GET_LOG = 4006;
  public static final int E4_FS_DATASERVICE_FILE_EXISTS_FOR_INITIALIZE = 4010;
  public static final int E4_FS_DATASERVICE_CRASHED_INITIALIZE = 4011;
  public static final int E4_FS_DATASERVICE_CRASHED_PATCH = 4015;

  public static final int E4_DB_BLAH = 4200;
}
