package pet.project.hlib2filestorage.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pet.project.hlib2filestorage.model.dto.auth.UserLoginDto;

@Controller
@RequestMapping("/sign-in")
public class LoginController {

    @GetMapping
    public String login(Model model) {
        model.addAttribute("user", new UserLoginDto());

        return "sign-in";
    }


    @GetMapping("/not-found")
    public String userNotFound(Model model,HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("exception", "User with such email not found");
        model.addAttribute("user", new UserLoginDto());
        return "sign-in";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }
}
