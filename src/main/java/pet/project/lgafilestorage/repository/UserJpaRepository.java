package pet.project.lgafilestorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pet.project.lgafilestorage.model.entity.User;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User findByUsername(String username);
}


/*Description:

The bean 'usersRepository', defined in pet.project.lgafilestorage.repository.UsersRepository defined in @EnableRedisRepositories declared on
 RedisRepositoriesRegistrar.EnableRedisRepositoriesConfiguration, could not be registered.
 A bean with that name has already been defined in pet.project.lgafilestorage.repository.UsersRepository defined in @EnableJpaRepositories declared on
 JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration and overriding is disabled.

        Action:

Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true

Disconnected from the target VM, address: '127.0.0.1:53959', transport: 'socket'

Process finished with exit code 1

ЧТо это за ошибка?
 */