package pet.project.hlib2filestorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pet.project.hlib2filestorage.exception.DatabaseException;
import pet.project.hlib2filestorage.model.dto.auth.UserRegistrationDto;
import pet.project.hlib2filestorage.model.entity.User;
import pet.project.hlib2filestorage.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public void save(UserRegistrationDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .roles(Set.of("ROLE_USER"))
                .build();

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new DatabaseException("User with such email or username already exist");
        }
    }
}
