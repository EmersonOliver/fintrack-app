package br.com.fintrack.core.security;

import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.user.service.UserService;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Provider
@ApplicationScoped
public class JwtTokenFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserService userService;

    @Inject
    AuthSecurityContext securityContext;

    @Context
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        if (containerRequestContext.getUriInfo().getPath().contains("login")) {
            return;
        }
        String tokenUserId = jsonWebToken.getSubject();
        if (tokenUserId == null) {
            throw new WebApplicationException("Token inválido", 401);
        }
        UserEntity userEntity = userService.loadById(UUID.fromString(tokenUserId));
        if (userEntity != null && !tokenUserId.equals(userEntity.getUserId().toString())) {
            throw new WebApplicationException("Usuário não autorizado", 403);
        }
        if (userEntity == null) {
            throw new WebApplicationException("Usuario não autorizado", 403);
        }
        buildCustomSecurityContext(userEntity);
    }

    private void buildCustomSecurityContext(UserEntity userEntity) {
        securityContext.initialize();
        securityContext.getInstance().get().jsonWebToken = jsonWebToken;
        securityContext.getInstance().get().ipAdress = request.remoteAddress().toString();
        securityContext.getInstance().get().userName = userEntity.getName();
        securityContext.getInstance().get().userId = userEntity.getUserId();
    }
}
