package pet.project.lgafilestorage.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(DatabaseException.class)
    public RedirectView databaseException(HttpServletRequest request, RedirectAttributes redirectAttributes, Exception exception) {
        redirectAttributes.addFlashAttribute("exception", exception.getMessage());
        String referer = request.getHeader("Referer");

        if (referer == null || referer.isEmpty()) {
            referer = "/";
        }

        return new RedirectView(referer, true);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public RedirectView userNotFoundException(HttpServletRequest request, RedirectAttributes redirectAttributes, Exception exception) {
        redirectAttributes.addFlashAttribute("exception", exception.getMessage());
        String referer = request.getHeader("Referer");

        if (referer == null || referer.isEmpty()) {
            referer = "/";
        }

        return new RedirectView(referer, true);
    }
    @ExceptionHandler(FolderOperationException.class)
    public RedirectView folderAlreadyExistsException(HttpServletRequest request, RedirectAttributes redirectAttributes, Exception exception) {
        redirectAttributes.addFlashAttribute("exception", exception.getMessage());
        String referer = request.getHeader("Referer");

        if (referer == null || referer.isEmpty()) {
            referer = "/";
        }

        return new RedirectView(referer, true);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("exception", "File size exceeds the maximum limit of 700 MB.");
        return "redirect:/";
    }

}
