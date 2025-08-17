package com.ecommerce.auth_service.security;

import com.ecommerce.auth_service.entity.UserLoginCache;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        RequiredPermission permissionAnnotation = method.getAnnotation(RequiredPermission.class);

        if (permissionAnnotation == null) {
            return true;
        }

        String requiredPermission = permissionAnnotation.value();
        UserLoginCache userSession = (UserLoginCache) request.getAttribute("USER_SESSION");

        if (userSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: User session not found");
            return false;
        }

        List<String> userPermissions = userSession.getUserRedis().getPermissions();

        String requiredPrefix = requiredPermission.split(":")[0];

        if (userPermissions.contains(requiredPermission)) {
            return true;
        }

        boolean hasAllPermission = userPermissions.stream()
                .anyMatch(p -> {
                    String[] parts = p.split(":");
                    return parts.length == 2 && parts[0].equals(requiredPrefix) && parts[1].equalsIgnoreCase("all");
                });

        if (hasAllPermission) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Forbidden: Insufficient permissions");
        return false;
    }
}
