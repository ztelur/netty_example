/**
 * Superid.menkor.com Inc.
 * Copyright (c) 2012-2018 All Rights Reserved.
 */
package com.remcarpediem.heartbeat.server.server;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author libing
 * @version $Id: CustomProtocol.java, v 0.1 2018年11月23日 上午10:37 zt Exp $
 */
@Data
public class CustomProtocol implements Serializable {
    private static final Long serialVersionUID = 4671171056588401542L;
    private Long id;
    private String content;

    public CustomProtocol(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public CustomProtocol() {
    }
}