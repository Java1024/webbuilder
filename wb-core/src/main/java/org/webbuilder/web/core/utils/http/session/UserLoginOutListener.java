package org.webbuilder.web.core.utils.http.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.po.user.User;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class UserLoginOutListener implements HttpSessionListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /* Session创建事件 */
    public void sessionCreated(HttpSessionEvent se) {

    }

    /* Session失效事件 */
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        try {
            User user = WebUtil.getLoginUser(session);
            if (user != null) {
                HttpSessionManagerContainer.getSessionManager().removeUser(user.getU_id());
            }
            HttpSessionManagerContainer.getSessionManager().removeSession(session.getId());
        } catch (Exception e) {
            logger.error("remove session or user error!", e);
        }
    }
}