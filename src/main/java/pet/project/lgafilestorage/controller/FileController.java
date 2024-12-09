package pet.project.lgafilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
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
public class FileController { //todo добавить валидацию

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<ByteArrayResource> getFile(FileRequestDto fileRequestDto) {
        FileDownloadDto fileDownloadDto = fileService.getFile(fileRequestDto);

        if (fileRequestDto.getFileName().getBytes().length > fileRequestDto.getFileName().length()) {
            String fileName = fileRequestDto.getFileName();
            fileRequestDto.setFileName("file" + getFileExtension(fileName));
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileRequestDto.getFileName()
                )
                .contentType(MediaType.valueOf(fileDownloadDto.getContentType()))
                .contentLength(fileDownloadDto.getContentLength())
                .body(fileDownloadDto.getFile());
    }

    @PostMapping
    public String uploadFile(@Valid @ModelAttribute("fileUploadDto") FileUploadDto objectDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("objectErrors", bindingResult.getFieldErrors());
            return "redirect:/";
        }
        fileService.uploadFile(objectDto);

        redirectAttributes.addFlashAttribute("redirectFolderPath", getFileLocation(objectDto));
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deleteFile(FileRequestDto fileRequestDto, RedirectAttributes redirectAttributes) {

        fileService.deleteFile(fileRequestDto);

        redirectAttributes.addFlashAttribute("redirectFolderPath", getFileLocation(fileRequestDto));
        return "redirect:/";
    }

    @GetMapping("/rename")
    public String renameFile(@Valid FileRenameDto dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("redirectFolderPath", getFileLocation(dto));

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("bindingErrors", bindingResult.getAllErrors());
            return "redirect:/";
        }

        fileService.renameFile(dto);
        return "redirect:/";
    }

    private String getFileLocation(FileRequestDto dto) {
        String path = dto.getFilePath();
        String name = dto.getFileName();

        if (path != null && name != null &&
                !path.isEmpty() && !name.isEmpty()) {

            if (!path.contains(name)) {
                return path;
            }
            return path.substring(0, path.lastIndexOf(name));
        }
        return "";
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
