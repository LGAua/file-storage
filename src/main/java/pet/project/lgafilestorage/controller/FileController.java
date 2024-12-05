package pet.project.lgafilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.lgafilestorage.model.dto.file.FileDownloadDto;
import pet.project.lgafilestorage.model.dto.file.FileRenameDto;
import pet.project.lgafilestorage.model.dto.file.FileRequestDto;
import pet.project.lgafilestorage.model.dto.file.FileUploadDto;
import pet.project.lgafilestorage.model.dto.folder.FolderContentDto;
import pet.project.lgafilestorage.model.dto.folder.FolderRequestDto;
import pet.project.lgafilestorage.service.FileService;
import pet.project.lgafilestorage.service.FolderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<ByteArrayResource> getFile(FileRequestDto fileRequestDto) {
        FileDownloadDto fileDownloadDto = fileService.getFile(fileRequestDto);

        if (fileRequestDto.getFileName().getBytes().length > fileRequestDto.getFileName().length()) {
            fileRequestDto.setFileName("file");
        }
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileRequestDto.getFileName()
                )
                .contentType(MediaType.valueOf(fileDownloadDto.getContentType()))
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
        fileService.uploadFile(objectDto);

        redirectAttributes.addFlashAttribute("uploadFileSuccess", "Operation successful");
        String folderPath = objectDto.getFolderPath();
        return "redirect:/?path=" +  getRedirectPath(folderPath);
    }

    //todo DeleteMapping with JS
    @GetMapping("/delete")
    public String deleteFile(FileRequestDto fileRequestDto) {

        fileService.deleteFile(fileRequestDto);
        String folderPath = getFileLocation(fileRequestDto).getFolderPath();
        return "redirect:/?path=" + getRedirectPath(folderPath);
    }

    //todo PutMapping with JS
    @GetMapping("/rename")
    public String renameFolder(FileRenameDto dto) {

        fileService.renameFile(dto);
        String folderPath = getFileLocation(dto).getFolderPath();
        return "redirect:/?path=" + getRedirectPath(folderPath);
    }

    private FolderRequestDto getFileLocation(FileRequestDto dto) {
        String filePath = dto.getFilePath();
        String fileName = dto.getFileName();

        String folderPath = filePath == null ? "" : filePath.replace(fileName, "");
        return new FolderRequestDto(null, folderPath, dto.getUsername());
    }

    private String getRedirectPath(String path){
        return path == null ? "" : path;
    }
}
