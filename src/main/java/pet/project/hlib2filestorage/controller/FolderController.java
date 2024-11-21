package pet.project.hlib2filestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.hlib2filestorage.model.dto.folder.FolderContentDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderRequestDto;
import pet.project.hlib2filestorage.service.FolderService;

@Controller
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @GetMapping("/folder")
    public String getFile(FolderRequestDto folderRequestDto,
                          RedirectAttributes redirectAttributes) {
        FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/home";
    }
}
