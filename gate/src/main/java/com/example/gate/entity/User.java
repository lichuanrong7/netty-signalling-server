package com.example.gate.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class User implements Serializable {
    private String userId;
    private String devId;
    private String roomId;
    private String username;
    private String platform;
    private String token;
    private Long registerTime;
}