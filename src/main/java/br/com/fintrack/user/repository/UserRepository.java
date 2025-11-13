package br.com.fintrack.user.repository;

import br.com.fintrack.user.domain.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserEntity, UUID> {
    public UserEntity findByEmail(String email) {
        return find("from UserEntity where UPPER(email)=:email", Parameters.with("email", email.toUpperCase()))
                .firstResultOptional().orElse(null);
    }
}
