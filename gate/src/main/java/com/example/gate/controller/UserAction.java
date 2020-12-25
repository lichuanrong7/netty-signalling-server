package com.example.gate.controller;

import com.example.common.entity.ImNode;
import com.example.common.entity.LoginBack;
import com.example.common.im.bean.User;
import com.example.common.util.JsonUtil;
import com.example.gate.balance.ImLoadBalance;
import com.example.gate.controller.utility.BaseController;
import com.example.gate.entity.UserPO;
import com.example.gate.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserAction extends BaseController {

    @Resource
    private UserService userService;
    @Resource
    private ImLoadBalance imLoadBalance;

    @RequestMapping(value = "/login/{platform}/{roomId}/{userId}/{devId}",method = RequestMethod.GET)
    public String loginAction(
            @PathVariable("platform") String platform,
            @PathVariable("roomId") String roomId,
            @PathVariable("userId") String userId,
            @PathVariable("devId") String devId) {
        LoginBack back = null;
        UserPO userPo = new UserPO();
        userPo.setPlatform(platform);
        userPo.setRoomId(roomId);
        userPo.setUserId(userId);
        userPo.setDevId(devId);
        com.example.gate.entity.User user = userService.login(userPo);
        if(user!=null){
            back = new LoginBack();
            com.example.common.im.bean.User u = new User();
            u.setUserId(user.getUserId());
            u.setToken(user.getToken());
            u.setDevId(user.getDevId());
            u.setPlatform(0);
            //取得最佳的Netty服务器
            ImNode bestWorker = imLoadBalance.getBestWorker();
            back.setImNode(bestWorker);
            back.setUser(u);
            back.setToken(user.getToken());
        }
        return (back!=null)? JsonUtil.pojoToJson(back):null;
    }

    @RequestMapping(value = "/removeWorkers",method = RequestMethod.GET)
    public String removeWorkers(){
        imLoadBalance.removeWorkers();
        return "ok";
    }

    @RequestMapping(value = "/nodeInfo",method = RequestMethod.GET)
    public ImNode getNode(){
        ImNode bestWorker = imLoadBalance.getBestWorker();
        return bestWorker;
    }



}
