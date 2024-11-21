package pet.project.hlib2filestorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.project.hlib2filestorage.model.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);


    User findByUsername(String username);
}
