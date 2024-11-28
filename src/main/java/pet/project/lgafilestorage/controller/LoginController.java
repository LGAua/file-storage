package pet.project.lgafilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pet.project.lgafilestorage.model.dto.auth.UserLoginDto;

@Controller
@RequestMapping("/sign-in")
public class LoginController {

    @GetMapping
    public String login(Model model) {

        model.addAttribute("user", new UserLoginDto());
        return "sign-in";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }
}
