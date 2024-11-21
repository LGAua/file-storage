package pet.project.hlib2filestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.hlib2filestorage.model.dto.file.FileRequestDto;
import pet.project.hlib2filestorage.model.dto.file.FileResponseDto;
import pet.project.hlib2filestorage.model.dto.folder.*;
import pet.project.hlib2filestorage.model.dto.file.FileUploadDto;
import pet.project.hlib2filestorage.service.FileService;
import pet.project.hlib2filestorage.service.FolderService;

import java.util.List;

@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final FolderService folderService;
    private final FileService fileService;

    @GetMapping
    public String homePage(Model model) {

        model.addAttribute("folderCreateDto", new FolderCreateDto());
        model.addAttribute("fileUploadDto", new FileUploadDto());
        model.addAttribute("folderUploadDto", new FolderUploadDto());
        model.addAttribute("folderRequestDto", new FolderRequestDto());
        model.addAttribute("fileRequestDto", new FileRequestDto());

        return "home";
    }

//    @PostMapping("/create-folder")
//    public String createFile(@Valid @ModelAttribute("fileCreateDto") FolderCreateDto fileDto,
//                             BindingResult bindingResult,
//                             RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute("fileErrors", bindingResult.getFieldErrors());
//            return "redirect:/home";
//        }
//        folderService.createFolder(fileDto.getFolderName());
//
//        return "redirect:/home";
//    }

    @PostMapping("/upload-file")
    public String uploadObject(@Valid @ModelAttribute("fileUploadDto") FileUploadDto objectDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("objectErrors", bindingResult.getFieldErrors());
            return "redirect:/home";
        }
        fileService.saveObject(objectDto);
        redirectAttributes.addFlashAttribute("uploadFileSuccess", "Operation successful");
        return "redirect:/home";
    }

    @PostMapping("/upload-folder")
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

    @GetMapping("/folders")
    public String getObjectsInsideFolder(FolderRequestDto folderRequestDto,
                                         RedirectAttributes redirectAttributes) {
        FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);

        return "redirect:/home";
    }

    @GetMapping("/folders/file")
    public ResponseEntity<byte[]> getFile(FileRequestDto fileRequestDto) {
        byte[] file = fileService.getFile(fileRequestDto);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileRequestDto.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}
