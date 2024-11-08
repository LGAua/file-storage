package pet.project.hlib2filestorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pet.project.hlib2filestorage.model.dto.UserLoginDto;
import pet.project.hlib2filestorage.model.dto.UserRegistrationDto;
import pet.project.hlib2filestorage.model.entity.User;
import pet.project.hlib2filestorage.repository.UserRepository;

import java.util.Optional;
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

        userRepository.save(user);
    }

//    public boolean verifyCredentials(UserLoginDto userLoginDto) {
//        User user = userRepository.findByEmail(userLoginDto.getEmail());
//
//        return isIdentical(userLoginDto, user);
//    }
//
//    private boolean isIdentical(UserLoginDto dto, User user) {
//        return dto.getUsername().equals(user.getEmail()) &&
//                dto.getPassword().equals(user.getPassword());
//    }
}
