package com.example.im.handler;

import com.example.common.cocurrent.FutureTaskScheduler;
import com.example.common.im.bean.msg.ProtoMsg;
import com.example.im.processer.ChatRedirectProcesses;
import com.example.im.server.session.LocalSession;
import com.example.im.server.session.ServerSession;
import com.example.im.server.session.SessionManger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("ChatRedirectHandler")
@ChannelHandler.Sharable
@SuppressWarnings("all")
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    ChatRedirectProcesses redirectProcesser;

    @Autowired
    SessionManger sessionManger;

    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        //判断消息类型
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = ((ProtoMsg.Message) msg).getType();
        if (!headType.equals(redirectProcesser.op())) {
            super.channelRead(ctx, msg);
            return;
        }
        //异步处理转发的逻辑
        FutureTaskScheduler.add(() ->
        {
            //判断是否登录
            LocalSession session = LocalSession.getSession(ctx);
            if (null != session && session.isLogin()) {
                redirectProcesser.action(session, pkg);
                return;
            }

            ProtoMsg.MessageRequest request = pkg.getMessageRequest();
            List<ServerSession> toSessions = SessionManger.inst().getSessionsBy(request.getTo());
            final boolean[] isSended = {false};
            toSessions.forEach((serverSession) -> {

                if (serverSession instanceof LocalSession) {// 将IM消息发送到接收方
                    serverSession.writeAndFlush(pkg);
                    isSended[0] =true;
                }
            });
            if(!isSended[0])
            {
                log.error("用户尚未登录，不能接受消息");
            }
        });
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LocalSession session = ctx.channel().attr(LocalSession.SESSION_KEY).get();
        if (null != session && session.isValid()) {
            session.close();
            sessionManger.removeLocalSession(session.getSessionId());
        }
    }

}
