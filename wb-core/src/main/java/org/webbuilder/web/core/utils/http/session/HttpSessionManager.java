package org.webbuilder.web.core.utils.http.session;

import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * httpSession管理，用于进行登陆用户判断，在线人数统计等
 * Created by 浩 on 2015-09-28 0028.
 */
public interface HttpSessionManager {

    /**
     * 根据登陆用户的ID 获取SessionId
     *
     * @param userId 登陆用户id
     * @return session ID
     */
    String getSessionIdByUserId(String userId) throws Exception;

    /**
     * 根据用户ID从session中删除一个用户(下线)
     *
     * @param userId 要删除的用户ID
     * @throws Exception 删除异常
     */
    void removeUser(String userId) throws Exception;

    /**
     * 根据sessionId删除Session
     *
     * @param sessionId 要删除的sessionID
     * @throws Exception 删除异常
     */
    void removeSession(String sessionId) throws Exception;

    /**
     * 添加一个用户
     *
     * @param userId  用户ID
     * @param session HttpSession
     * @throws Exception 添加异常
     */
    void addUser(String userId, HttpSession session) throws Exception;

    /**
     * 获取当前登录的所有用户ID集合
     *
     * @return 当前登录用户ID
     * @throws Exception 异常信息
     */
    Set<String> getUserIdList() throws Exception;

    /**
     * 获取当前登录用户数量
     *
     * @return 登陆用户数量
     * @throws Exception 异常信息
     */
    int getUserTotal() throws Exception;

    /**
     * 获取所有sessionId集合
     *
     * @return sessionId集合
     * @throws Exception 异常信息
     */
    Set<String> getSessionIdList() throws Exception;

    /**
     * 根据用户ID 判断用户是否已经登陆
     *
     * @param userId 用户ID
     * @return 是否已登陆
     */
    boolean isLogin(String userId);
}
