package pet.project.lgafilestorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories("pet.project.lgafilestorage.repository.redis")
@EnableCaching
public class FileStorage {

	public static void main(String[] args) {
		SpringApplication.run(FileStorage.class, args);
	}

}

//TODO :
// 1. Tests
// 2. Actuator
// 3. HTML modal
// 4. Refactor
// 5. Deploy