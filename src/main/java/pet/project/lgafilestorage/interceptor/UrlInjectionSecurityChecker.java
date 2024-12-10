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

    private static final Pattern pattern = Pattern.compile("user-(\\d+)-");
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getParameter("path");
        if (path == null) {
            return true;
        }
        // user-4-files/
        String pathId = path.substring(5, path.lastIndexOf("-files"));
        Principal userPrincipal = request.getUserPrincipal();
        User user = userService.findByUsername(userPrincipal.getName());

        if (user.getId() == Long.parseLong(pathId)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
