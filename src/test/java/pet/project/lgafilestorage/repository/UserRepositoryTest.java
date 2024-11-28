package pet.project.lgafilestorage.repository;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import pet.project.lgafilestorage.model.entity.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {


    //todo Добавить тест контейнер.

    @Autowired
    private UserRepository userRepository;

    private final User userSavedInDatabase = User.builder()
            .username("existingUser")
            .email("existingUser@test.com")
            .roles(Set.of("ROLE_USER"))
            .build();

    private final User userWithInvalidCredentials = User.builder()
            .username("existingUser")
            .email("existingUser@test.com")
            .roles(Set.of("ROLE_USER"))
            .build();


    @BeforeEach
    @Sql
    void setUp() {
        userRepository.save(userSavedInDatabase);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findByEmail() {
        User userFoundByEmail = userRepository.findByEmail("existingUser@test.com");

        assertThat(userFoundByEmail).isEqualTo(userSavedInDatabase);
    }

    @Test
    void saveUserWithViolatedConstraintsFields() {
        assertThrows(ConstraintViolationException.class, () -> userRepository.save(userWithInvalidCredentials));
    }

    @Test
    void saveUserWithCorrectField() {
        assertDoesNotThrow(() -> userRepository.save(userWithInvalidCredentials));
    }

}