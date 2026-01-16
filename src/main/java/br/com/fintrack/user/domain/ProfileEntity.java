package br.com.fintrack.user.domain;

import br.com.fintrack.user.resources.request.ProfileUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profile", schema = "fintrack")
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    @Column(name = "photo_profile_path")
    private String photoProfilePath;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "country")
    private String country;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "bio")
    private String bio;

    @Column(name = "birthday")
    private LocalDate birthday;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    public static ProfileEntity create(UserEntity user) {
        ProfileEntity p = new ProfileEntity();
        p.setUser(user);
        return p;
    }
    public void update(ProfileUpdateRequest r) {
        this.name = r.name();
        this.lastName = r.lastName();
        this.bio = r.bio();
        this.phone = r.phone();
        this.country = r.country();
        this.birthday = r.birthday();
    }

}
