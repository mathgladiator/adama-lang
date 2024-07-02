/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.common.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.*;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.DnsServerAddressStreamProviders;
import io.netty.util.concurrent.Future;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NettyDNSTxtResolver implements DNSTxtResolver {
  private final NioEventLoopGroup eventLoop;

  public NettyDNSTxtResolver() {
    this.eventLoop = new NioEventLoopGroup();
  }

  public void shutdown() {
    eventLoop.shutdownGracefully();
  }

  @Override
  public void query(String domain, Callback<String[]> callback) {
    DefaultDnsQuestion question = new DefaultDnsQuestion(domain, DnsRecordType.TXT);
    DnsNameResolver resolver = new DnsNameResolverBuilder() //
        .nameServerProvider(DnsServerAddressStreamProviders.platformDefault()) //
        .eventLoop(eventLoop.next()) //
        .channelFactory(new ReflectiveChannelFactory<>(NioDatagramChannel.class)) //
        .build();

    resolver.resolveAll(question).addListener((Future<List<DnsRecord>> future) -> {
      if (future.isDone()) {
        if (future.isSuccess()) {
          ArrayList<String> txt = new ArrayList<>();
          for (DnsRecord record : future.get()) {
            if (record.type() == DnsRecordType.TXT && record instanceof DefaultDnsRawRecord) {
              ByteBuf buf = ((DefaultDnsRawRecord) record).content();
              int sz = buf.readableBytes();
              byte[] bytes = new byte[sz];
              buf.readBytes(bytes);
              txt.add(new String(bytes, StandardCharsets.UTF_8));
            }
          }
          callback.success(txt.toArray(new String[txt.size()]));
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.DNS_RESOLVE_FAILED));
        }
      }
    });
  }
}
