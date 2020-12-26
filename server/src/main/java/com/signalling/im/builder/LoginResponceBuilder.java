package com.example.im.builder;

import com.example.common.im.ProtoInstant;
import com.example.common.im.bean.msg.ProtoMsg;
import org.springframework.stereotype.Service;

@SuppressWarnings("all")
@Service("LoginResponceBuilder")
public class LoginResponceBuilder {

    public ProtoMsg.Message loginResponce(ProtoInstant.ResultCodeEnum en,long seqId,String sessionId){
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.LOGIN_REQUEST)//设置消息类型
                .setSequence(seqId)
                .setSessionId(sessionId);

        ProtoMsg.LoginResponse.Builder b = ProtoMsg.LoginResponse.newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);

        mb.setLoginResponse(b.build());
        return mb.build();
    }
}
