package br.com.fintrack.user.service;

import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.user.resources.request.UserRequest;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    void createNewUser(UserRequest userRequest);

    UserEntity loadUserByMailAndPass(String email, String password);

    UserEntity loadById(UUID uuid);

    String getTokenUser(String email, String password);

    boolean login(String email, String password);

    Optional<UserEntity> findByEmail(String email);
}
