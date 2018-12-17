/**
 * Superid.menkor.com Inc.
 * Copyright (c) 2012-2018 All Rights Reserved.
 */
package com.remcarpediem.bolt.server;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.NamedThreadFactory;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.remcarpediem.bolt.common.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author libing
 * @version $Id: SimpleServerUserProcessor.java, v 0.1 2018年11月29日 下午5:00 zt Exp $
 */
public class SimpleServerUserProcessor extends SyncUserProcessor<RequestBody> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleServerUserProcessor.class);

    private Long delayMs;

    private boolean delaySwitch;

    private ThreadPoolExecutor executor;

    private boolean timeoutDiscard = true;

    private AtomicInteger invokeTimes = new AtomicInteger();

    private AtomicInteger onewayTimes = new AtomicInteger();

    private AtomicInteger syncTimes = new AtomicInteger();

    private AtomicInteger futureTimes = new AtomicInteger();

    private AtomicInteger callbackTimes = new AtomicInteger();

    private String remoteAddr;

    private CountDownLatch latch = new CountDownLatch(1);


    public SimpleServerUserProcessor() {
        this.delaySwitch = false;
        this.delayMs = 0L;
        this.executor = new ThreadPoolExecutor(1, 3, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4), new NamedThreadFactory("Request-process-pool"));
    }


    public SimpleServerUserProcessor(long delay) {
        this();
        if (delay < 0) {
            throw new IllegalArgumentException("delay time illegal!");
        }
        this.delaySwitch = true;
        this.delayMs = delay;
    }


    public SimpleServerUserProcessor(long delay, int core, int max, int keepaliveSeconds,
                                     int workQueue) {
        this(delay);
        this.executor = new ThreadPoolExecutor(core, max, keepaliveSeconds, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(workQueue), new NamedThreadFactory(
                "Request-process-pool"));
    }


    @Override
    public Object handleRequest(BizContext bizContext, RequestBody requestBody) throws Exception {
        logger.warn("Request received:" + requestBody + ", timeout:" + bizContext.getClientTimeout()
                + ", arriveTimestamp:" + bizContext.getArriveTimestamp());

        if (bizContext.isRequestTimeout()) {
            String errMsg = "Stop process in server biz thread, already timeout";
            processTimes(requestBody);
            logger.warn(errMsg);
            throw new Exception(errMsg);
        }

        this.remoteAddr = bizContext.getRemoteAddress();

        // test biz context get connection
        Assert.isTrue(bizContext.getConnection() != null);
        Assert.isTrue(bizContext.getConnection().isFine());

        Long waitTime = (Long) bizContext.getInvokeContext().get(InvokeContext.BOLT_PROCESS_WAIT_TIME);

        if (logger.isInfoEnabled()) {
            logger.info("Server User processor process wait time {}", waitTime);
        }

        latch.countDown();

        logger.warn("Server User processor say, remote address is [" + this.remoteAddr + "].");

        Assert.isTrue(RequestBody.class.equals(requestBody.getClass()));

        processTimes(requestBody);

        if (!delaySwitch) {
            return RequestBody.DEFAULT_SERVER_RETURN_STR;
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return RequestBody.DEFAULT_SERVER_RETURN_STR;
    }

    @Override
    public String interest() {
        return RequestBody.class.getName();
    }

    @Override
    public boolean timeoutDiscard() {
        return this.timeoutDiscard;
    }

    public int getInvokeTimes() {
        return this.invokeTimes.get();
    }

    public int getInvokeTimesEachCallType(RequestBody.InvokeType type) {
        return new int[] { this.onewayTimes.get(), this.syncTimes.get(), this.futureTimes.get(),
                this.callbackTimes.get() }[type.ordinal()];
    }

    public String getRemoteAddr() throws InterruptedException {
        latch.await(100, TimeUnit.MILLISECONDS);
        return this.remoteAddr;
    }


    private void processTimes(RequestBody req) {
        this.invokeTimes.incrementAndGet();
        if (req.getMsg().equals(RequestBody.DEFAULT_ONEWAY_STR)) {
            this.onewayTimes.incrementAndGet();
        } else if (req.getMsg().equals(RequestBody.DEFAULT_SYNC_STR)) {
            this.syncTimes.incrementAndGet();
        } else if (req.getMsg().equals(RequestBody.DEFAULT_FUTURE_STR)) {
            this.futureTimes.incrementAndGet();
        } else if (req.getMsg().equals(RequestBody.DEFAULT_CALLBACK_STR)) {
            this.callbackTimes.incrementAndGet();
        }
    }


    // ~~~ getters and setters
    /**
     * Getter method for property <tt>timeoutDiscard</tt>.
     *
     * @return property value of timeoutDiscard
     */
    public boolean isTimeoutDiscard() {
        return timeoutDiscard;
    }

    /**
     * Setter method for property <tt>timeoutDiscard<tt>.
     *
     * @param timeoutDiscard value to be assigned to property timeoutDiscard
     */
    public void setTimeoutDiscard(boolean timeoutDiscard) {
        this.timeoutDiscard = timeoutDiscard;
    }

}