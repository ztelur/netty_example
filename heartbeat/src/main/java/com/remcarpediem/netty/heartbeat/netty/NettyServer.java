/**
 * Superid.menkor.com Inc.
 * Copyright (c) 2012-2018 All Rights Reserved.
 */
package com.remcarpediem.netty.heartbeat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 *
 * @author libing
 * @version $Id: NettyServer.java, v 0.1 2018年11月22日 下午5:41 zt Exp $
 */
@Component
public class NettyServer {

    private static final Logger log = Logger.getLogger(NettyServer.class);


    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    public ChannelFuture run(InetSocketAddress address) {
        ChannelFuture f = null;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChann)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            f = bootstrap.bind(address).syncUninterruptibly();
            channel = f.channel();
        } catch (Exception e) {

        } finally {

        }

        return f;
    }
}