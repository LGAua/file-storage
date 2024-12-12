package pet.project.lgafilestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pet.project.lgafilestorage.model.dto.MinioObjectDto;
import pet.project.lgafilestorage.model.dto.file.FileRequestDto;
import pet.project.lgafilestorage.service.SearchService;

import java.util.Set;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public String searchPage(@AuthenticationPrincipal User user,
                             @RequestParam(value = "name", required = false) String objectName,
                             Model model) {

        if (user == null) {
            return "redirect:/sign-in";
        }

        if (objectName != null && !objectName.isEmpty()) {
            FileRequestDto fileRequestDto = new FileRequestDto();
            fileRequestDto.setObjectName(objectName);
            fileRequestDto.setUsername(user.getUsername());

            Set<MinioObjectDto> foundObjects = searchService.findObjectsByName(fileRequestDto);
            model.addAttribute("foundObjects", foundObjects);
        }

        return "search";
    }

}
