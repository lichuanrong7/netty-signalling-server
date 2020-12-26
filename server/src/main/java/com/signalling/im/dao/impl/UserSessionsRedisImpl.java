package com.example.im.dao.impl;

import com.example.common.util.JsonUtil;
import com.example.im.dao.UserSessionsDAO;
import com.example.im.distributed.ImWorker;
import com.example.im.server.session.UserSessions;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository("UserSessionsRedisImpl")
public class UserSessionsRedisImpl implements UserSessionsDAO {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static final String REDIS_PREFIX = "UserSessions:uid:";

    private static final long CASHE_LONG = 60 * 4;//4小时

    @Override
    public void save(UserSessions s) {
        String key = REDIS_PREFIX + s.getUserId();
        String value = JsonUtil.pojoToJson(s);
        redisTemplate.opsForValue().set(key,value,CASHE_LONG, TimeUnit.MINUTES);
    }

    @Override
    public UserSessions get(String usID) {
        String key = REDIS_PREFIX + usID;
        String value = (String) redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(value)) {
            return JsonUtil.jsonToPojo(value, UserSessions.class);
        }
        return null;
    }

    @Override
    public void cacheUser(String uid, String sessionId) {
        UserSessions us = get(uid);
        if (null == us) {
            us = new UserSessions(uid);
        }
        us.addSession(sessionId, ImWorker.getInst().getLocalNodeInfo());
        save(us);
    }

    @Override
    public void removeUserSession(String uid, String sessionId) {
        UserSessions us = get(uid);
        if (null == us) {
            us = new UserSessions(uid);
        }
        us.removeSession(sessionId);
        save(us);
    }

    @Override
    public UserSessions getAllSession(String userId) {
        UserSessions us = get(userId);
        if (null != us) {
            return us;
        }
        return null;
    }
}
