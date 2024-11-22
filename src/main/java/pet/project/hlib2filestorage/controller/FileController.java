package pet.project.hlib2filestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.hlib2filestorage.model.dto.file.FileRequestDto;
import pet.project.hlib2filestorage.model.dto.file.FileUploadDto;
import pet.project.hlib2filestorage.service.FileService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<byte[]> getFile(FileRequestDto fileRequestDto) {
        byte[] file = fileService.getFile(fileRequestDto);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileRequestDto.getFileName() + "\""
                ).contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }


    @PostMapping
    public String uploadFile(@Valid @ModelAttribute("fileUploadDto") FileUploadDto objectDto,
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

    @GetMapping("/delete")
    public String deleteFile(FileRequestDto fileRequestDto) {
        fileService.deleteFile(fileRequestDto);
        return "redirect:/home";
    }
}
