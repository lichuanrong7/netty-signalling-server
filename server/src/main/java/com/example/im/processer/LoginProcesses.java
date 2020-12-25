package com.example.im.processer;

import com.example.common.im.ProtoInstant;
import com.example.common.im.bean.User;
import com.example.common.im.bean.msg.ProtoMsg;
import com.example.im.builder.LoginResponceBuilder;
import com.example.im.server.session.LocalSession;
import com.example.im.server.session.ServerSession;
import com.example.im.server.session.SessionManger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Slf4j
@Service("LoginProcesses")
@SuppressWarnings("all")
public class LoginProcesses extends AbstractServerProcesses {

    @Autowired
    LoginResponceBuilder loginResponceBuilder;
    @Autowired
    SessionManger sessionManger;

    @Override
    public ProtoMsg.HeadType op() {
        return ProtoMsg.HeadType.LOGIN_REQUEST;
    }

    @Override
    public Boolean action(LocalSession session,
                          ProtoMsg.Message proto) {
        // 取出token验证
        ProtoMsg.LoginRequest info = proto.getLoginRequest();
        long seqNo = proto.getSequence();

        User user = User.fromMsg(info);

        //检查用户
        boolean isValidUser = checkUser(user);
        if (!isValidUser) {
            ProtoInstant.ResultCodeEnum resultcode =
                    ProtoInstant.ResultCodeEnum.NO_TOKEN;
            ProtoMsg.Message response =
                    loginResponceBuilder.loginResponce(resultcode, seqNo, "-1");
            //发送之后，断开连接
            session.writeAndClose(response);
            return false;
        }

        session.setUser(user);

        /**
         * 绑定session
         */
        session.bind();
        sessionManger.addLocalSession(session);


        /**
         * 通知客户端：登录成功
         */

        ProtoInstant.ResultCodeEnum resultCode = ProtoInstant.ResultCodeEnum.SUCCESS;
        ProtoMsg.Message response =
                loginResponceBuilder.loginResponce(resultCode, seqNo, session.getSessionId());
        session.writeAndFlush(response);
        return true;
    }

    private boolean checkUser(User user) {

        //校验用户,比较耗时的操作,需要100 ms以上的时间
        //方法1：调用远程用户restfull 校验服务
        //方法2：调用数据库接口校验

        List<ServerSession> l = sessionManger.getSessionsBy(user.getUserId());
        if (null != l && l.size() > 0) {
            return false;
        }
        return true;

    }
}
