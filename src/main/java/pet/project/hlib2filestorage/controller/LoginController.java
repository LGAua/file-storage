package pet.project.hlib2filestorage.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.hlib2filestorage.model.dto.UserLoginDto;
import pet.project.hlib2filestorage.service.UserService;

@Controller
@RequestMapping("/sign-in")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String login(Model model) {
        model.addAttribute("user", new UserLoginDto());

        return "sign-in";
    }

//    @PostMapping
//    public String verifyCredentials(@ModelAttribute("user") @Valid UserLoginDto userLoginDto,
//                                    BindingResult bindingResult,
//                                    RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute("error", bindingResult.getFieldErrors());
//            return "redirect:/sign-in";
//        }
//
//        if (!userService.verifyCredentials(userLoginDto)){
//            redirectAttributes.addFlashAttribute("wrong-credentials", "Email or password incorrect");
//            return "redirect:/sign-in";
//        }
//
//        return "redirect/home";
//    }
}
