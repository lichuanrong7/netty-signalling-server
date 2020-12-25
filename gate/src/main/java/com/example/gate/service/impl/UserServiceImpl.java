package com.example.gate.service.impl;

import com.example.common.im.ProtoInstant;
import com.example.common.util.JsonUtil;
import com.example.gate.entity.Room;
import com.example.gate.entity.User;
import com.example.gate.entity.UserPO;
import com.example.gate.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.jws.Oneway;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    private Long timeout = 3600L;

    @Override
    public User login(UserPO userPO) {
        String userId = userPO.getUserId();
        String devId = userPO.getDevId();
        String roomId = userPO.getRoomId();
        String username = userPO.getUsername();
        String platform = userPO.getPlatform();
        User user = new User();
        String key = "signalling:user:".concat(userId).concat(":").concat(devId);
        Object obj = redisTemplate.opsForValue().get(key);
        if(obj!=null){
            user = JsonUtil.jsonToPojo(obj.toString(), User.class);
        } else {
            user.setUserId(userId);
            user.setDevId(devId);
            user.setRoomId(roomId);
            user.setUsername(username);
            user.setPlatform(platform);
            user.setRegisterTime(System.currentTimeMillis());
            String token = UUID.randomUUID().toString().toLowerCase().replaceAll("-","");
            user.setToken(token);
            String value = JsonUtil.pojoToJson(user);
            redisTemplate.opsForValue().set(key,value,timeout, TimeUnit.SECONDS);
            addRoom(roomId,userId, platform);
        }
        return user;
    }

    @Override
    public List<Room> getRoomInfo(String roomId) {
        List<Room> rooms = new ArrayList<>();
        try{
            String key = "signalling:room:".concat(roomId);
            Set sets = redisTemplate.opsForSet().members(key);
            sets.forEach(s->{
                Object obj = s;
                Room r = JsonUtil.jsonToPojo(obj.toString(),Room.class);
                rooms.add(r);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
        return rooms;
    }

    private void addRoom(String roomId,String userId,String platform){
        try {
            String key = "signalling:room:".concat(roomId);
            Room room = new Room();
            room.setRoomId(roomId);
            room.setUserId(userId);
            room.setPlatform(platform);
            String value = JsonUtil.pojoToJson(room);
            boolean f = redisTemplate.opsForSet().isMember(key,value);
            if(!f){
                redisTemplate.opsForSet().add(key,value);
                redisTemplate.expire(key,timeout,TimeUnit.SECONDS);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
