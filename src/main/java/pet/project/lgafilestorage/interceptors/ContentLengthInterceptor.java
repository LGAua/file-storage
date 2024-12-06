//package pet.project.lgafilestorage.interceptors;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//public class ContentLengthInterceptor implements HandlerInterceptor {
//
//    private static final long MAX_FILE_SIZE = 700 * 1024 * 1024;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (request.getMethod().equals("POST") &&
//                (request.getRequestURI().endsWith("/file") || request.getRequestURI().endsWith("/folder"))) {
//
//            if (request.getContentLength() > MAX_FILE_SIZE) {
//                response.sendError(417, "Exceed allowed Content-Length");
//                return false;
//            }
//        }
//        return true;
//    }
//}
