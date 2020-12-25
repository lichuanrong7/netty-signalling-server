package com.example.gate.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Room implements Serializable {
    private String roomId;
    private String userId;
    private String platform;
}
