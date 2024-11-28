package pet.project.lgafilestorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.project.lgafilestorage.model.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);


    User findByUsername(String username);
}
