package com.example.common.im.bean;

import lombok.Data;

@Data
public class Notification<T> {
    public static final int SESSION_ON = 10;//上线的通知
    public static final int SESSION_OFF = 20;//下线的通知
    public static final int CONNECT_FINISHED = 30;//节点的链接成功
    private int type;
    private T data;

    public Notification() {
    }

    public Notification(T t) {
        data = t;
    }
}
