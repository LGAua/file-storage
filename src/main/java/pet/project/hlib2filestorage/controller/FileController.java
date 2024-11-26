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
import pet.project.hlib2filestorage.model.dto.file.FileDownloadDto;
import pet.project.hlib2filestorage.model.dto.file.FileRequestDto;
import pet.project.hlib2filestorage.model.dto.file.FileUploadDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderContentDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderRequestDto;
import pet.project.hlib2filestorage.service.FileService;
import pet.project.hlib2filestorage.service.FolderService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final FolderService folderService;

    @GetMapping
    public ResponseEntity<byte[]> getFile(FileRequestDto fileRequestDto) {
        FileDownloadDto fileDownloadDto = fileService.getFile(fileRequestDto);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileRequestDto.getFileName()
                ).contentType(MediaType.valueOf(fileDownloadDto.getContentType()))
                .contentLength(fileDownloadDto.getContentLength())
                .body(fileDownloadDto.getFile());
    }


    //todo Cannot upload
    @PostMapping
    public String uploadFile(@Valid @ModelAttribute("fileUploadDto") FileUploadDto objectDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("objectErrors", bindingResult.getFieldErrors());
            return "redirect:/";
        }
        fileService.saveObject(objectDto);
        redirectAttributes.addFlashAttribute("uploadFileSuccess", "Operation successful");
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deleteFile(FileRequestDto fileRequestDto,
                             RedirectAttributes redirectAttributes) {
        fileService.deleteFile(fileRequestDto);

        String path = getFileLocation(fileRequestDto);

        FolderRequestDto folderRequestDto = new FolderRequestDto();
        folderRequestDto.setFolderPath(path);
        folderRequestDto.setUsername(fileRequestDto.getUsername());

        FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

    private String getFileLocation(FileRequestDto dto) {
        String filePath = dto.getFilePath();
        String fileName = dto.getFileName();

        return filePath.substring(0, filePath.indexOf(fileName));
    }
}
