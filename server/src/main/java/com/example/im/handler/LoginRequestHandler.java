package com.example.im.handler;

import com.example.common.cocurrent.CallbackTask;
import com.example.common.cocurrent.CallbackTaskScheduler;
import com.example.common.im.bean.msg.ProtoMsg;
import com.example.im.processer.LoginProcesses;
import com.example.im.server.session.LocalSession;
import com.example.im.server.session.SessionManger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("LoginRequestHandler")
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter{

    @Autowired
    LoginProcesses loginProcesses;

    /**
     * 收到消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;

        //取得请求类型
        ProtoMsg.HeadType headType = pkg.getType();

        if (!headType.equals(loginProcesses.op())) {
            super.channelRead(ctx, msg);
            return;
        }

        LocalSession session = new LocalSession(ctx.channel());

        //异步任务，处理登录的逻辑
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                return loginProcesses.action(session, pkg);
            }
            //异步任务返回
            @Override
            public void onBack(Boolean r) {
                if (r) {
                    ctx.pipeline().remove(LoginRequestHandler.this);
                    log.info("登录成功:" + session.getUser());
                } else {
                    SessionManger.inst().closeSession(ctx);
                    log.info("登录失败:" + session.getUser());
                }
            }
            //异步任务异常
            @Override
            public void onException(Throwable t) {
                SessionManger.inst().closeSession(ctx);
                log.info("登录失败:" + session.getUser());
            }
        });
    }

}
