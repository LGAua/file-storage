package pet.project.hlib2filestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.hlib2filestorage.model.dto.UserRegistrationDto;
import pet.project.hlib2filestorage.service.UserService;

@Controller
@RequestMapping("/sign-up")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping
    public String registration(Model model){
        model.addAttribute("user", new UserRegistrationDto());

        return "sign-up";
    }

    @PostMapping
    public String verifyCredentials(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("errors", bindingResult.getFieldErrors());
            return "redirect:/sign-up";
        }

        userService.save(userDto);

        return "redirect:/sign-in";
    }
}
