package pet.project.hlib2filestorage.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pet.project.hlib2filestorage.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void registration() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/sign-up"))
                .andExpect(model().attributeExists("user"))
                .andExpect(status().isOk());
    }

    @Test
    void verifyInvalidCredentials() throws Exception {
        mvc.perform(post("/sign-up")
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .formField("username", "")
                        .formField("email", "")
                        .formField("password", ""))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(redirectedUrl("/sign-up"));
    }

    @Test
    void verifyValidCredentials() throws Exception {
        mvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .formField("username", "existingUser")
                        .formField("email", "existingUser@test.com")
                        .formField("password", "existingUserP433word"))
                .andExpect(flash().attributeCount(0))
                .andExpect(redirectedUrl("/sign-in"));
    }

}