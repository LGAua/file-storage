package pet.project.lgafilestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pet.project.lgafilestorage.model.dto.file.FileRequestDto;
import pet.project.lgafilestorage.model.dto.folder.*;
import pet.project.lgafilestorage.model.dto.file.FileUploadDto;
import pet.project.lgafilestorage.service.FolderService;
import pet.project.lgafilestorage.service.UserService;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final FolderService folderService;
    private final UserService userService;

    @GetMapping
    public String homePage(@AuthenticationPrincipal User user,
                           @RequestParam(required = false) String path,
                           @ModelAttribute("redirectFolderPath") String redirectPath,
                           Model model) {

        if (user != null) {
            model.addAttribute("avatar", userService.findByUsername(user.getUsername()).getAvatarUrl());

            FolderRequestDto folderRequestDto = new FolderRequestDto();
            folderRequestDto.setUsername(user.getUsername());
            folderRequestDto.setFolderPath(path);

            if (!redirectPath.isEmpty()) folderRequestDto.setFolderPath(redirectPath);

            FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
            model.addAttribute("folderContentDto", folderContentDto);
        }

        model.addAttribute("fileUploadDto", new FileUploadDto());
        model.addAttribute("folderUploadDto", new FolderUploadDto());

        model.addAttribute("fileRequestDto", new FileRequestDto());
        model.addAttribute("folderRequestDto", new FolderRequestDto());

        return "home";
    }
    //todo Check username from security and username in dto. To prevent access to folders of other users.
}
