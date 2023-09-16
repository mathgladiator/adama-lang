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
package org.adamalang.common.net;

import io.netty.buffer.ByteBuf;

/** contract for sending/getting bytes; this requires some reading as there are caveats */
public interface ByteStream {
  /** request some bytes from the remote side. This is a hint for application level flow control. */
  void request(int bytes);

  /** create is required to be called to create the buffer that you provide next */
  ByteBuf create(int bestGuessForSize);

  /** send the buffer created by create(). This oddity exists precisey because we need create to prepend some information before you fill in the body */
  void next(ByteBuf buf);

  /** the stream is complete */
  void completed();

  /** the stream produced an error */
  void error(int errorCode);
}
