package com.example.im.processer;

import com.example.common.im.bean.msg.ProtoMsg;
import com.example.im.server.session.LocalSession;

@SuppressWarnings("all")
public interface ServerReciever {
    ProtoMsg.HeadType op();
    Boolean action(LocalSession ch, ProtoMsg.Message proto);

}
