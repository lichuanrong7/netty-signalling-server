package com.example.common.entity;

import com.example.common.im.bean.User;
import lombok.Data;

@Data
public class LoginBack {
    ImNode imNode;
    private String token;
    private User user;
}
