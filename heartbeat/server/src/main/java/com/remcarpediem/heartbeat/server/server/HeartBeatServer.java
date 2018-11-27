/**
 * Superid.menkor.com Inc.
 * Copyright (c) 2012-2018 All Rights Reserved.
 */
package com.remcarpediem.heartbeat.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 *
 * @author libing
 * @version $Id: HeartBeatServer.java, v 0.1 2018年11月23日 下午1:51 zt Exp $
 */
@Component
public class HeartBeatServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatServer.class);

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup();

    @Value("${netty.server.port}")
    private int nettyPort;

    @PostConstruct
    public void start() throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap()
                            .group(boss, worker)
                            .channel(NioServerSocketChannel.class)
                            .localAddress(new InetSocketAddress(nettyPort))
                            // 保持长连接
                            .childOption(ChannelOption.SO_KEEPALIVE, true)
                            .childHandler(new HeartbeatInitializer());

        ChannelFuture future = bootstrap.bind().sync();

        if (future.isSuccess()) {
            LOGGER.info("启动 Netty 成功");
        }
    }

    @PreDestroy
    public void destroy() {
        boss.shutdownGracefully().syncUninterruptibly();
        worker.shutdownGracefully().syncUninterruptibly();
        LOGGER.info("关闭 Netty 成功");
    }


}