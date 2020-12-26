package com.example.im.server.session;

import com.example.common.entity.ImNode;
import com.example.im.distributed.ImWorker;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class UserSessions {

    private String userId;
    private Map<String, ImNode> map = new LinkedHashMap<>(10);

    public UserSessions(String userId){
        this.userId = userId;
    }

    public void addSession(String sessionId,ImNode node){
        map.put(sessionId,node);
    }

    public void removeSession(String sessionId){
        map.remove(sessionId);
    }

    public void addLocalSession(LocalSession session){
        map.put(session.getSessionId(), ImWorker.getInst().getLocalNodeInfo());
    }

}
