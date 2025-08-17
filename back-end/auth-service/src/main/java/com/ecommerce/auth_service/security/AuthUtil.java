package com.ecommerce.auth_service.security;

import com.ecommerce.auth_service.entity.UserLoginCache;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class AuthUtil {

    private static final String USER_SESSION_KEY = "USER_SESSION";

    public static UserLoginCache getUserSession() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Object userSession = request.getAttribute(USER_SESSION_KEY);
        if (userSession instanceof UserLoginCache) {
            return (UserLoginCache) userSession;
        }
        return null;
    }

    public static Long getCurrentUserId() {
        UserLoginCache userSession = getUserSession();
        if (userSession != null && userSession.getUserRedis() != null) {
            return userSession.getUserRedis().getId();
        }
        return null;
    }
}
