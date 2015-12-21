package org.webbuilder.web.core.utils.http.session.impl;

import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.core.utils.http.session.HttpSessionManager;
import org.webbuilder.web.po.user.User;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地session管理器实现
 * Created by 浩 on 2015-09-28 0028.
 */
public class LocalHttpSessionManager implements HttpSessionManager {

    /**
     * httpSession存储器，sessionId:HttpSession
     */
    private static final Map<String, HttpSession> sessionStorage = new ConcurrentHashMap<>();

    /**
     * 用户ID与session管理存储器，userId:HttpSession
     */
    private static final Map<String, HttpSession> userSessionStorage = new ConcurrentHashMap<>();

    @Override
    public String getSessionIdByUserId(String userId) throws Exception {
        HttpSession session = userSessionStorage.get(userId);
        if (session != null) {
            User user = WebUtil.getLoginUser(session);
            if (user == null) {
                userSessionStorage.remove(userId);
                return null;
            }
            return user.getU_id();
        }
        return null;
    }

    @Override
    public void removeUser(String userId) throws Exception {
        HttpSession session = userSessionStorage.get(userId);
        if (session != null) {
            try {
                session.invalidate();
            } catch (Exception e) {
            } finally {
                sessionStorage.remove(session.getId());
                userSessionStorage.remove(userId);
            }
        }
    }

    @Override
    public void removeSession(String sessionId) throws Exception {
        HttpSession session = sessionStorage.get(sessionId);
        if (session != null) {
            User user = WebUtil.getLoginUser(session);
            if (user != null) {
                userSessionStorage.remove(user.getU_id());
            }
            sessionStorage.remove(sessionId);
        }
    }

    @Override
    public void addUser(String userId, HttpSession session) throws Exception {
        sessionStorage.put(session.getId(), session);
        removeUser(userId);//踢出已经登陆
        userSessionStorage.put(userId, session);
    }

    @Override
    public Set<String> getUserIdList() throws Exception {
        return userSessionStorage.keySet();
    }

    @Override
    public int getUserTotal() throws Exception {
        return userSessionStorage.size();
    }

    @Override
    public Set<String> getSessionIdList() throws Exception {
        return sessionStorage.keySet();
    }

    @Override
    public boolean isLogin(String userId) {
        return userSessionStorage.containsKey(userId);
    }
}
