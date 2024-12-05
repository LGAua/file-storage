package pet.project.lgafilestorage.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ContentLengthInterceptor implements HandlerInterceptor {

    private static final long MAX_FILE_SIZE = 700 * 1024 * 1024;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getContentLength() > MAX_FILE_SIZE) {
            response.setStatus(417);
            return false;
        }
        return true;
    }
}
