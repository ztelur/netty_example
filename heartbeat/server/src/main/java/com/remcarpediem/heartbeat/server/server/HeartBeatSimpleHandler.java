/**
 * Superid.menkor.com Inc.
 * Copyright (c) 2012-2018 All Rights Reserved.
 */
package com.remcarpediem.heartbeat.server.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author libing
 * @version $Id: HeartBeatSimpleHandler.java, v 0.1 2018年11月23日 下午1:20 zt Exp $
 */
public class HeartBeatSimpleHandler extends SimpleChannelInboundHandler<CustomProtocol> {
    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandler.class);
    private static final ByteBuf HEART_BEAT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(new CustomProtocol(123L, "Ping").toString(),
            CharsetUtil.UTF_8));


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CustomProtocol customProtocol) throws Exception {
        LOGGER.info("收到customProtocol={}", customProtocol);
        // 保存客户端与channel之间的关系
        NettySocketHolder.put(customProtocol.getId(), (NioSocketChannel) channelHandlerContext.channel());
    }

    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            if (IdleState.READER_IDLE.equals(idleStateEvent.state())) {
                LOGGER.info("已经5秒没有收到消息!");
                // 向客户端收取消息
                ctx.writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}