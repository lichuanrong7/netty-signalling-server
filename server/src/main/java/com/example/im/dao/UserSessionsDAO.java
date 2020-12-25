package com.example.im.dao;

import com.example.im.server.session.UserSessions;

public interface UserSessionsDAO {
    void save(UserSessions s);
    UserSessions get(String sessionid);
    void cacheUser(String uid, String sessionId);
    void removeUserSession(String uid, String sessionId);
    UserSessions getAllSession(String userId);
}
