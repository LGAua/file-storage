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
import pet.project.hlib2filestorage.model.dto.folder.*;
import pet.project.hlib2filestorage.service.FolderService;

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

    //todo i can upload folder but when delete the last file inside the folder is disappearing
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

    //todo Can not create folder probleb with getFolderLocation(folderRequestDto)
    @PostMapping("/new-folder")
    public String createFile(@Valid FolderRequestDto folderRequestDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("fileErrors", bindingResult.getFieldErrors());
            return "redirect:/";
        }
        folderService.createFolder(folderRequestDto);

        folderRequestDto.setFolderPath(getFolderLocation(folderRequestDto));

        FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

     // todo NOT WORKING for folder
    @GetMapping("/delete")
    public String deleteFolder(FolderRequestDto folderRequestDto,
                               RedirectAttributes redirectAttributes) {
        folderService.deleteFolder(folderRequestDto);

        String path = getFolderLocation(folderRequestDto);
        folderRequestDto.setFolderPath(path);
        folderRequestDto.setUsername(folderRequestDto.getUsername());

        FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

    @GetMapping("/rename")
    public String renameFolder(FolderRenameRequestDto dto,
                               RedirectAttributes redirectAttributes) {
        folderService.renameFolder(dto);

        FolderContentDto folderContentDto = folderService.getFolderContent(dto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

    private String getFolderLocation(FolderRequestDto dto) {
        String folderPath = dto.getFolderPath();
        String folderName = dto.getFolderName();

        return folderPath.substring(0, folderPath.indexOf(folderName));
    }
}
