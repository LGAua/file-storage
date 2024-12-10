package pet.project.lgafilestorage.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.collection.spi.PersistentBag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor()
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @OneToOne
    @JoinColumn(name = "avatar_picture_id")
    private AvatarPicture avatarPicture;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Role> roles;
}

