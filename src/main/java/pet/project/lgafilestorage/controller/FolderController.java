package pet.project.lgafilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.lgafilestorage.model.dto.folder.*;
import pet.project.lgafilestorage.service.FolderService;

import java.net.http.HttpRequest;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderController {

    private final FolderService folderService;

    @GetMapping
    public String getFolder() {
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

        String folderPath = folderUploadDto.getFolderPath();
        return "redirect:/?path=" + getRedirectPath(folderPath);
    }

    // todo При создании файла сюда почему то приходит folderPath корневой папки. На клиенте валидный путь, а сюда приходит измененный
    @PostMapping("/new-folder")
    public String createFile(@Valid FolderRequestDto dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("fileErrors", bindingResult.getFieldErrors());
            return "redirect:/";
        }
        String newPath = dto.getFolderPath() + dto.getFolderName() + "/";
        FolderRequestDto folderDto = folderService.createFolder(new FolderRequestDto(dto.getFolderName(), newPath, dto.getUsername()));

        String folderPath = folderDto.getFolderPath();
        return "redirect:/?path=" + getRedirectPath(folderPath);
    }

    @GetMapping("/delete")
    public String deleteFolder(FolderRequestDto folderRequestDto) {

        folderService.deleteFolder(folderRequestDto);
        String folderPath = folderRequestDto.getFolderPath();
        return "redirect:/?path=" + getRedirectPath(folderPath);
    }

    // todo При переименовании пустой папки она удаляется
    @PutMapping("/rename")
    public String renameFolder(FolderRenameRequestDto dto, HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        folderService.renameFolder(dto);
        String folderPath = getFolderLocation(dto).getFolderPath();
        return "redirect:/?path=" + getRedirectPath(folderPath);
    }

    private FolderRequestDto getFolderLocation(FolderRequestDto dto) {
        String folderPath = dto.getFolderPath();
        String folderName = dto.getFolderName();
        String folderLocation = folderPath.replace(folderName + "/", "");
        dto.setFolderPath(folderLocation);

        return dto;
    }

    private String getRedirectPath(String path){
        return path == null ? "" : path;
    }
}
