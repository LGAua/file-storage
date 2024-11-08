package pet.project.hlib2filestorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.project.hlib2filestorage.model.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    User findByEmail(String email);
}
