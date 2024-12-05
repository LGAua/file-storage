package pet.project.lgafilestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pet.project.lgafilestorage.model.dto.MinioObjectDto;
import pet.project.lgafilestorage.model.dto.file.FileRequestDto;
import pet.project.lgafilestorage.model.dto.folder.*;
import pet.project.lgafilestorage.model.dto.file.FileUploadDto;
import pet.project.lgafilestorage.repository.UserRepository;
import pet.project.lgafilestorage.service.FileService;
import pet.project.lgafilestorage.service.FolderService;

import java.util.Set;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final FolderService folderService;
    private final UserRepository userRepository;

    @GetMapping
    public String homePage(@AuthenticationPrincipal User user,
                           @RequestParam(required = false) String path,
                           Model model) {

        if (user != null) {
            model.addAttribute("avatar", userRepository.findByUsername(user.getUsername()).getAvatarUrl());

            FolderRequestDto folderRequestDto = new FolderRequestDto();
            folderRequestDto.setUsername(user.getUsername());
            folderRequestDto.setFolderPath(path);

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
