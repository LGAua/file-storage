package pet.project.hlib2filestorage.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mvc;


    @Test
    void getLoginPage() throws Exception {
        mvc.perform(get("/sign-in"))
                .andExpect(view().name("sign-in"))
                .andExpect(model().attributeExists("user"))
                .andExpect(status().isOk());
    }

    @Test
    void securityLoginValidCredentials() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/sign-in")
                .formField("username","existingUser@test.com")
                .formField("password","existingUserP433word"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/home"));
    }

    @Test
    void securityLoginInvalidCredentials() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/sign-in")
                .formField("username","notExistingUser@test.com")
                .formField("password","notExistingUserP433word"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/sign-in/not-found"));
    }



    @Test
    void userNotFound() throws Exception {
        mvc.perform(get("/sign-in/not-found"))
                .andExpect(view().name("sign-in"))
                .andExpect(status().isNotFound());
    }
}