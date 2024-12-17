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
        UserRedis userRedis = UserRedis.builder()
                .id(userJpa.getId().toString())
                .username(userJpa.getUsername())
                .email(userJpa.getEmail())
                .password(userJpa.getPassword())
                .roles(userJpa.getRoles().stream()
                        .map(role -> new RoleRedis(role.getId(), role.getRole()))
                        .collect(toList()))
                .build();

        if (userJpa.getAvatarPicture() != null) {
            userRedis.setAvatarPicture(avatarPictureBuilder(userJpa));
        }

        return userRedis;
    }

    public static User toUserJpa(UserRedis userRedis) {
        User user = User.builder()
                .id(Long.parseLong(userRedis.getId()))
                .username(userRedis.getUsername())
                .email(userRedis.getEmail())
                .password(userRedis.getPassword())
                .roles(userRedis.getRoles().stream()
                        .map(roleRedis -> new Role(roleRedis.getId(), roleRedis.getRole()))
                        .collect(toList()))
                .build();

        if (userRedis.getAvatarPicture() != null) {
            user.setAvatarPicture(avatarPictureBuilder(userRedis));
        }

        return user;
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
