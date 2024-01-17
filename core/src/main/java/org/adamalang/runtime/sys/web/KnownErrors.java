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
package org.adamalang.runtime.sys.web;

import org.adamalang.ErrorCodes;

/** for converting Adama's error codes to HTTP */
public class KnownErrors {
  public static int inferHttpStatusCodeFrom(int code) {
    if (code == ErrorCodes.DOCUMENT_WEB_GET_ABORT || //
        code == ErrorCodes.DOCUMENT_WEB_DELETE_ABORT || //
        code == ErrorCodes.DOCUMENT_WEB_PUT_ABORT) return 400;
    if (code == ErrorCodes.DOCUMENT_WEB_GET_NOT_FOUND || //
        code == ErrorCodes.DOCUMENT_WEB_DELETE_NOT_FOUND || //
        code == ErrorCodes.DOCUMENT_WEB_OPTIONS_NOT_FOUND || //
        code == ErrorCodes.DOCUMENT_WEB_PUT_NOT_FOUND) return 404;
    return 500;
  }
}
