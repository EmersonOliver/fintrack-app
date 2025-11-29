package br.com.fintrack.user.service.impl;

import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.user.service.AuthService;
import br.com.fintrack.user.service.UserService;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Set;

@Slf4j
@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    @Inject
    UserService userService;

    @Override
    public String getTokenUser(String email, String password) {
        UserEntity user = userService.loadUserByMailAndPass(email, password);

        if (user == null) {
            throw new WebApplicationException("Usuário não encontrado", 401);
        }

        if (!BcryptUtil.matches(password, user.getPasswordHash())) {
            throw new WebApplicationException("Credenciais inválidas", 401);
        }

        return Jwt
                .issuer("fintrack-api")
                .subject(user.getUserId().toString())      // ID do usuário
                .upn(email)                             // user principal name
                .claim("email", email)
                .groups(Set.of("USER"))
                .expiresIn(3600)                        // 1h
                .sign();
    }
}
