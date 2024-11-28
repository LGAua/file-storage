package pet.project.lgafilestorage.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(DatabaseException.class)
    public RedirectView databaseException(HttpServletRequest request, RedirectAttributes redirectAttributes, Exception exception) {
        redirectAttributes.addFlashAttribute("exception", exception.getMessage());
        String path = request.getServletPath();

        return new RedirectView(path, true);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public RedirectView userNotFoundException(HttpServletRequest request, RedirectAttributes redirectAttributes, Exception exception) {
        redirectAttributes.addFlashAttribute("exception", exception.getMessage());
        String path = request.getServletPath();

        return new RedirectView(path, true);
    }

    @ExceptionHandler(FolderOperationException.class)
    public RedirectView folderAlreadyExistsException(HttpServletRequest request, RedirectAttributes redirectAttributes, Exception exception) {
        redirectAttributes.addFlashAttribute("exception", exception.getMessage());
        String path = request.getServletPath();

        return new RedirectView(path, true);
    }


}
