package pet.project.hlib2filestorage.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(DatabaseException.class)
    public RedirectView databaseException(HttpServletRequest request, RedirectAttributes redirectAttributes, Exception exception){
        redirectAttributes.addFlashAttribute("exception",exception.getMessage());
        String path = request.getServletPath();

        return new RedirectView(path, true);
    }

    @ExceptionHandler(AuthenticationException.class)
    public RedirectView userNotFoundException(HttpServletRequest request, RedirectAttributes redirectAttributes, Exception exception){
        redirectAttributes.addFlashAttribute("exception",exception.getMessage());
        String path = request.getServletPath();

        return new RedirectView(path, true);
    }


}
