package pet.project.lgafilestorage.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pet.project.lgafilestorage.model.entity.User;
import pet.project.lgafilestorage.service.UserService;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UrlInjectionSecurityChecker implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getParameter("path");
        Principal userPrincipal = request.getUserPrincipal();

        if (path == null || userPrincipal == null) {
            return true;
        }

        User user = userService.findByUsername(userPrincipal.getName());
        String pathId = path.substring(5, path.lastIndexOf("-files"));

        if (user.getId() == Long.parseLong(pathId)) {
            return true;
        }

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access to files of other users is forbidden");
        return false;
    }
}
