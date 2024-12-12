package pet.project.lgafilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.lgafilestorage.model.dto.folder.*;
import pet.project.lgafilestorage.service.FolderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderController {

    private final FolderService folderService;

    @GetMapping
    public String getFolder() {
        return "redirect:/";
    }

    //todo check size limitations exception
    @PostMapping
    public String uploadFolder(@Valid @ModelAttribute("folderUploadDto") FolderUploadDto folderUploadDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("objectErrors", bindingResult.getFieldErrors());
            return "redirect:/";
        }
        folderService.uploadFolder(folderUploadDto);

        redirectAttributes.addFlashAttribute("redirectFolderPath", getFolderLocation(folderUploadDto));
        return "redirect:/";
    }

    @PostMapping("/new-folder")
    public String createFile(@Valid FolderRequestDto dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("fileErrors", bindingResult.getFieldErrors());
            return "redirect:/";
        }
        String newPath = dto.getFolderPath() + dto.getFolderName() + "/";
        folderService.createFolder(new FolderRequestDto(dto.getFolderName(), newPath, dto.getUsername()));

        redirectAttributes.addFlashAttribute("redirectFolderPath", dto.getFolderPath());
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deleteFolder(FolderRequestDto folderRequestDto, RedirectAttributes redirectAttributes) {

        folderService.deleteFolder(folderRequestDto);

        redirectAttributes.addFlashAttribute("redirectFolderPath", getFolderLocation(folderRequestDto));
        return "redirect:/";
    }

    @GetMapping("/rename")
    public String renameFolder(@Valid FolderRenameRequestDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("redirectFolderPath", getFolderLocation(dto));

        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("bindingErrors", bindingResult.getAllErrors());
            return "redirect:/";
        }

        folderService.renameFolder(dto);

        return "redirect:/";
    }

    private String getFolderLocation(FolderRequestDto dto) {
        String path = dto.getFolderPath();
        String name = dto.getFolderName();

        if (path != null && name != null &&
                !path.isEmpty() && !name.isEmpty()) {
            return path.substring(0, path.lastIndexOf(name));
        }
        return "";
    }
}
