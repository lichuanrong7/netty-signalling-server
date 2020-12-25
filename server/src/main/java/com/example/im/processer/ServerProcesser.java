package com.example.im.processer;

import com.example.common.im.bean.msg.ProtoMsg;
import com.example.im.server.session.LocalSession;

@SuppressWarnings("all")
public interface ServerProcesser {
    ProtoMsg.HeadType type();
    boolean action(LocalSession ch, ProtoMsg.Message proto);
}
