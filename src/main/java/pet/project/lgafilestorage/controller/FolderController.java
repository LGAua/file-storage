package pet.project.lgafilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.lgafilestorage.model.dto.folder.*;
import pet.project.lgafilestorage.service.FolderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderController {

    private final FolderService folderService;

    @GetMapping
    public String getFolder(FolderRequestDto folderRequestDto,
                            RedirectAttributes redirectAttributes) {
        FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

    @PostMapping
    public String uploadFolder(@Valid @ModelAttribute("folderUploadDto") FolderUploadDto folderUploadDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("objectErrors", bindingResult.getFieldErrors());
            return "redirect:/";
        }
        folderService.uploadFolder(folderUploadDto);

        redirectAttributes.addFlashAttribute("uploadFolderSuccess", "Operation successful");
        return "redirect:/";
    }

    @PostMapping("/new-folder")
    public String createFile(@Valid FolderRequestDto folderRequestDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("fileErrors", bindingResult.getFieldErrors());
            return "redirect:/";
        }
        String folderPath = folderRequestDto.getFolderPath();
        String folderName = folderRequestDto.getFolderName();
        folderRequestDto.setFolderPath(folderPath + folderName + "/");

        folderService.createFolder(folderRequestDto);

        FolderContentDto folderContentDto = folderService.getFolderContent(getFolderLocation(folderRequestDto));
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

    //todo DeleteMapping with JS
    @GetMapping("/delete")
    public String deleteFolder(FolderRequestDto folderRequestDto,
                               RedirectAttributes redirectAttributes) {

        folderService.deleteFolder(folderRequestDto);

        FolderContentDto folderContentDto = folderService.getFolderContent(getFolderLocation(folderRequestDto));
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

    // todo При переименовании пустой папки она удаляется
    //todo PutMapping with JS
    @GetMapping("/rename")
    public String renameFolder(FolderRenameRequestDto dto,
                               RedirectAttributes redirectAttributes) {

        folderService.renameFolder(dto);

        FolderContentDto folderContentDto = folderService.getFolderContent(getFolderLocation(dto));
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

    private FolderRequestDto getFolderLocation(FolderRequestDto dto) {
        String folderPath = dto.getFolderPath();
        String folderName = dto.getFolderName();
        String folderLocation = folderPath.substring(0, folderPath.indexOf(folderName));

        dto.setFolderPath(folderLocation);
        return dto;
    }
}
