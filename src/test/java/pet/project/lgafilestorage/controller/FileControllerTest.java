package pet.project.lgafilestorage.controller;

import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import pet.project.lgafilestorage.model.dto.file.FileDownloadDto;
import pet.project.lgafilestorage.model.dto.file.FileRequestDto;
import pet.project.lgafilestorage.model.dto.file.FileUploadDto;
import pet.project.lgafilestorage.repository.UserJpaRepository;
import pet.project.lgafilestorage.service.FileService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    private static final String URL = "/file";

    @Autowired
    private MockMvc mvc;


    private TestRestTemplate testRestTemplate;
    private TestRestTemplate testRestTemplateWithAuth;

    //    @MockBean
    @InjectMocks
    private FileService fileService;

    @Mock
    private UserJpaRepository userRepository;
    @Mock
    private MinioClient minioClient;


    @BeforeEach
    void setRestTemplate() {
        testRestTemplate = new TestRestTemplate();
        testRestTemplateWithAuth = new TestRestTemplate("testUser", "password", null);
    }

    @Test
    @Order(1)
    void uploadFile() throws IOException {
        byte[] picture = new byte[8];
        MockMultipartFile file = new MockMultipartFile("picture", "user_picture", "image/png", picture);
        FileUploadDto fileUploadDto = new FileUploadDto();
        ResponseEntity<String> responseEntity = testRestTemplateWithAuth.postForEntity(URL, fileUploadDto, String.class);

        FileDownloadDto savedFile =
                fileService.getFile(new FileRequestDto("picture", "folder/picture", "testUser"));

        assertThat(savedFile.getContentType()).isEqualTo(file.getContentType());
        assertThat(savedFile.getFile().getByteArray()).isEqualTo(file.getBytes());
    }

    @Test
    void httpGetRequestForFile_ResponseStatusOk() throws Exception {
        ResponseEntity<ByteArrayResource> response = testRestTemplateWithAuth.getForEntity(URL, ByteArrayResource.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteFile() {
    }

    @Test
    void renameFolder() {
    }
}