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
import pet.project.lgafilestorage.service.FileService;
import pet.project.lgafilestorage.service.FolderService;

import java.util.Set;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final FolderService folderService;
    private final FileService fileService;
    //todo вынести в HTML форму rename folder/file в отдельные компоненты и подставлять через th:if="${object.isDir} == true"
    @GetMapping
    public String homePage(@AuthenticationPrincipal User user,
                           @RequestParam(required = false) String path,
                           Model model) {
        if (!model.containsAttribute("folderContentDto")) {
            FolderRequestDto folderRequestDto = new FolderRequestDto();
            folderRequestDto.setUsername(user.getUsername());
            folderRequestDto.setFolderPath(path);

            FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
            model.addAttribute("folderContentDto", folderContentDto);
        }

        model.addAttribute("fileUploadDto", new FileUploadDto());
        model.addAttribute("folderUploadDto", new FolderUploadDto());
        model.addAttribute("folderRequestDto", new FolderRequestDto());
        model.addAttribute("fileRequestDto", new FileRequestDto());

        return "home";
    }

    @GetMapping("/search")
    public String searchPage(@AuthenticationPrincipal User user,
                             @RequestParam("name") String objectName,
                             Model model) {
        FileRequestDto fileRequestDto = new FileRequestDto();
        fileRequestDto.setObjectName(objectName);
        fileRequestDto.setUsername(user.getUsername());

        Set<MinioObjectDto> foundObjects = fileService.findObjectsByName(fileRequestDto);

        model.addAttribute("foundObjects", foundObjects);
        return "search";
    }

    //todo Check username from security and username in dto. To prevent access to folders of other users.
}
