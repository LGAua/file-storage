package pet.project.lgafilestorage.util;

import pet.project.lgafilestorage.model.entity.User;
import pet.project.lgafilestorage.model.redis.UserRedis;

public class UserConverter {
    public static UserRedis toUserRedis(User userJpa) {
        return UserRedis.builder()
                .id(userJpa.getId().toString())
                .username(userJpa.getUsername())
                .email(userJpa.getEmail())
                .password(userJpa.getPassword())
                .avatarUrl(userJpa.getAvatarUrl())
                .roles(userJpa.getRoles())
                .build();
    }

    public static User toUserJpa(UserRedis userRedis) {
        return User.builder()
                .id(Long.parseLong(userRedis.getId()))
                .username(userRedis.getUsername())
                .email(userRedis.getEmail())
                .password(userRedis.getPassword())
                .avatarUrl(userRedis.getAvatarUrl())
                .roles(userRedis.getRoles())
                .build();
    }
}
