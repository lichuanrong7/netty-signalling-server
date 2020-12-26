package com.example.im.builder;

import com.example.common.im.ProtoInstant;
import com.example.common.im.bean.msg.ProtoMsg;

@SuppressWarnings("all")
public class ChatMsgBuilder {

    public static ProtoMsg.Message buildChatResponse(long seqId, ProtoInstant.ResultCodeEnum en){
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.MESSAGE_RESPONSE) //设置消息类型
                .setSequence(seqId) ;

        ProtoMsg.MessageResponse.Builder rb = ProtoMsg.MessageResponse.newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);

        mb.setMessageResponse(rb.build());
        return mb.build();
    }

    public static ProtoMsg.Message buildLoginResponce(ProtoInstant.ResultCodeEnum en ,long seqId){
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.MESSAGE_RESPONSE)
                .setSequence(seqId);

        ProtoMsg.LoginResponse.Builder rb = ProtoMsg.LoginResponse.newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);

        mb.setLoginResponse(rb.build());
        return mb.build();
    }
}
