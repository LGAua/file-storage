package pet.project.hlib2filestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.hlib2filestorage.model.dto.folder.FolderContentDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderRequestDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderResponseDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderUploadDto;
import pet.project.hlib2filestorage.service.FolderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderController {

    private final FolderService folderService;

    @GetMapping
    public String getFile(FolderRequestDto folderRequestDto,
                          RedirectAttributes redirectAttributes) {
        FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/home";
    }

    @PostMapping
    public String uploadFolder(@Valid @ModelAttribute("folderUploadDto") FolderUploadDto folderUploadDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("objectErrors", bindingResult.getFieldErrors());
            return "redirect:/home";
        }
        folderService.uploadFolder(folderUploadDto);
        redirectAttributes.addFlashAttribute("uploadFolderSuccess", "Operation successful");
        return "redirect:/home";
    }

    @PostMapping("/new-folder")
    public String createFile(@Valid @ModelAttribute("fileCreateDto") FolderRequestDto fileRequestDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("fileErrors", bindingResult.getFieldErrors());
            return "redirect:/home";
        }
        folderService.createFolder(fileRequestDto);

        return "redirect:/home";
    }

    @GetMapping("/delete")
    public String deleteFolder(FolderRequestDto folderRequestDto){
        folderService.deleteFolder(folderRequestDto);

        return "redirect:/home";
    }
}
