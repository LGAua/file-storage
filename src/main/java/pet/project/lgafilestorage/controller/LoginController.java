package pet.project.lgafilestorage.controller;

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
