package pet.project.lgafilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final FolderService folderService;

    @GetMapping
    public ResponseEntity<byte[]> getFile(FileRequestDto fileRequestDto) {
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
        return "redirect:/";
    }

    //todo DeleteMapping with JS
    @GetMapping("/delete")
    public String deleteFile(FileRequestDto fileRequestDto,
                             RedirectAttributes redirectAttributes) {
        fileService.deleteFile(fileRequestDto);

        FolderRequestDto folderRequestDto = getFileLocation(fileRequestDto);

        FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

    //todo PutMapping with JS
    @GetMapping("/rename")
    public String renameFolder(FileRenameDto dto,
                               RedirectAttributes redirectAttributes) {

        fileService.renameFile(dto);
        FolderRequestDto folderRequestDto = getFileLocation(dto);

        FolderContentDto folderContentDto = folderService.getFolderContent(folderRequestDto);
        redirectAttributes.addFlashAttribute("folderContentDto", folderContentDto);
        return "redirect:/";
    }

    private FolderRequestDto getFileLocation(FileRequestDto dto) {
        String filePath = dto.getFilePath();
        String fileName = dto.getFileName();

        String folderPath = filePath.substring(0, filePath.indexOf(fileName));
        return new FolderRequestDto(null, folderPath, dto.getUsername());
    }
}
