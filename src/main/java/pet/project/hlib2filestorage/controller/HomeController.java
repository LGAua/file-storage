package pet.project.hlib2filestorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pet.project.hlib2filestorage.model.dto.file.FileRequestDto;
import pet.project.hlib2filestorage.model.dto.folder.*;
import pet.project.hlib2filestorage.model.dto.file.FileUploadDto;

@Controller
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public String homePage(Model model) {

        model.addAttribute("folderCreateDto", new FolderCreateDto());
        model.addAttribute("fileUploadDto", new FileUploadDto());
        model.addAttribute("folderUploadDto", new FolderUploadDto());
        model.addAttribute("folderRequestDto", new FolderRequestDto());
        model.addAttribute("fileRequestDto", new FileRequestDto());

        return "home";
    }

    //todo Check username from security and username in dto. To prevent access to folders of other users.
}
