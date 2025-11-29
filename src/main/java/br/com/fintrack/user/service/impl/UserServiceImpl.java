package br.com.fintrack.user.service.impl;

import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.user.repository.UserRepository;
import br.com.fintrack.user.resources.request.UserRequest;
import br.com.fintrack.user.service.AuthService;
import br.com.fintrack.user.service.UserService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;


@ApplicationScoped
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AuthService authService;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createNewUser(UserRequest userRequest) {
        var exists = userRepository.findByEmail(userRequest.email());
        if (exists != null) {
            throw new UsersException("Já existe um e-mail cadastrado, gostaria de efetuar login?");
        }
        String passHashed = BcryptUtil.bcryptHash(userRequest.password());
        var userEntity = UserEntity.builder().name(userRequest.name())
                .email(userRequest.email())
                .passwordHash(passHashed)
                .createdAt(LocalDate.now())
                .build();
        userRepository.persistAndFlush(userEntity);
    }

    @Override
    public UserEntity loadUserByMailAndPass(String email, String password) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        BcryptUtil.matches(password, user.getPasswordHash());
        return user;
    }

    @Override
    public UserEntity loadById(UUID uuid) {
        return userRepository.findById(uuid);
    }

    @Override
    public boolean login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        return BcryptUtil.matches(password, user.getPasswordHash());
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    @Override
    public String getTokenUser(String email, String password) {
        return authService.getTokenUser(email, password);
    }
}
