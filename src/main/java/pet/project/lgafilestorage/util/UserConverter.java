package pet.project.lgafilestorage.util;

import pet.project.lgafilestorage.model.entity.AvatarPicture;
import pet.project.lgafilestorage.model.entity.Role;
import pet.project.lgafilestorage.model.entity.User;
import pet.project.lgafilestorage.model.redis.AvatarPictureRedis;
import pet.project.lgafilestorage.model.redis.RoleRedis;
import pet.project.lgafilestorage.model.redis.UserRedis;

import static java.util.stream.Collectors.toList;

public class UserConverter {
    public static UserRedis toUserRedis(User userJpa) {
        return UserRedis.builder()
                .id(userJpa.getId().toString())
                .username(userJpa.getUsername())
                .email(userJpa.getEmail())
                .password(userJpa.getPassword())
                .avatarPicture(avatarPictureBuilder(userJpa))
                .roles(userJpa.getRoles().stream()
                        .map(role -> new RoleRedis(role.getId(), role.getRole()))
                        .collect(toList()))
                .build();
    }

    public static User toUserJpa(UserRedis userRedis) {
        return User.builder()
                .id(Long.parseLong(userRedis.getId()))
                .username(userRedis.getUsername())
                .email(userRedis.getEmail())
                .password(userRedis.getPassword())
                .avatarPicture(avatarPictureBuilder(userRedis))
                .roles(userRedis.getRoles().stream()
                        .map(roleRedis -> new Role(roleRedis.getId(), roleRedis.getRole()))
                        .collect(toList()))
                .build();
    }

    private static AvatarPicture avatarPictureBuilder(UserRedis userRedis) {
        return AvatarPicture.builder()
                .id(userRedis.getAvatarPicture().getId())
                .url(userRedis.getAvatarPicture().getUrl())
                .contentType(userRedis.getAvatarPicture().getContentType())
                .contentSize(userRedis.getAvatarPicture().getContentSize())
                .build();
    }

    private static AvatarPictureRedis avatarPictureBuilder(User userJpa) {
        return AvatarPictureRedis.builder()
                .id(userJpa.getAvatarPicture().getId())
                .url(userJpa.getAvatarPicture().getUrl())
                .contentType(userJpa.getAvatarPicture().getContentType())
                .contentSize(userJpa.getAvatarPicture().getContentSize())
                .build();
    }
}
