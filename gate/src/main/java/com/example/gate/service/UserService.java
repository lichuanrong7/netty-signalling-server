package com.example.gate.service;

import com.example.gate.entity.Room;
import com.example.gate.entity.User;
import com.example.gate.entity.UserPO;

import java.util.List;

public interface UserService {
    User login(UserPO userPO);
    List<Room> getRoomInfo(String roomId);
}
