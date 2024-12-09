//package pet.project.lgafilestorage.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import pet.project.lgafilestorage.model.entity.User;
//import pet.project.lgafilestorage.service.UserService;
//
//import java.security.Principal;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Component
//@RequiredArgsConstructor
//public class UrlInjectionSecurityChecker implements HandlerInterceptor {
//
//    private final UserService userService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        StringBuffer requestURL = request.getRequestURL();
//        String requestURI = request.getRequestURI();
//        Principal userPrincipal = request.getUserPrincipal();
//        userPrincipal.getName();
//        User user = userService.findByUsername(userPrincipal.getName());
//
//        return user.getId() == getUrlUserId(requestURI);
//    }
//
//    private Long getUrlUserId(String url) {
//        requestURI.matches("user-%s-files");
//        String id = url.substring(url.indexOf("user-")).substring(0, url.indexOf("-files"));
//        return Long.parseLong(id);
//    }
//}
