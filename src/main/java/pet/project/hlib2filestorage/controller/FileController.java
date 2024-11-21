package pet.project.hlib2filestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pet.project.hlib2filestorage.service.FileService;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
//
//    @GetMapping("/file")
//    public ResponseEntity<byte[]> getFile(@RequestParam("path") String folderPath){
//        fil
//    }
}
