package br.com.fintrack.user.repository;

import br.com.fintrack.user.domain.ProfileEntity;
import br.com.fintrack.user.domain.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProfileRepository implements PanacheRepositoryBase<ProfileEntity, UUID> {
    public Optional<ProfileEntity> findByUser(UserEntity user) {
        return find("user.userId=?1", user.getUserId()).singleResultOptional();
    }
}
