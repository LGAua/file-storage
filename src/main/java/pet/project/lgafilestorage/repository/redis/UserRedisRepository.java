package pet.project.lgafilestorage.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import pet.project.lgafilestorage.model.redis.UserRedis;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public Optional<UserRedis> findByUsername(String username) {
        UserRedis user = (UserRedis) redisTemplate.opsForValue().get(username);
        return Optional.ofNullable(user);
    }

    public void save(UserRedis user) {
        user.setTimeout(5 * 60L);
        redisTemplate.opsForValue().set(user.getUsername(), user);
    }
}
