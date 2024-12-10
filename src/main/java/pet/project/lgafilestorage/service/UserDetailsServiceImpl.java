package pet.project.lgafilestorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pet.project.lgafilestorage.model.entity.Role;
import pet.project.lgafilestorage.model.entity.User;
import pet.project.lgafilestorage.model.redis.UserRedis;
import pet.project.lgafilestorage.repository.UserJpaRepository;
import pet.project.lgafilestorage.repository.redis.UserRedisRepository;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static pet.project.lgafilestorage.util.UserConverter.toUserRedis;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;
    private final UserRedisRepository userRedisRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userJpaRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User with email: " + email + "is not found");
        }
        userRedisRepository.save(toUserRedis(user));

        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(Role::getRole)
                .map(SimpleGrantedAuthority::new)
                .collect(toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
