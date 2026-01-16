package br.com.fintrack.user.service.impl;

import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.user.domain.ProfileEntity;
import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.user.repository.UserRepository;
import br.com.fintrack.user.resources.request.UserRequest;
import br.com.fintrack.user.service.AuthService;
import br.com.fintrack.user.service.ProfileService;
import br.com.fintrack.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ApplicationScoped
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AuthService authService;
    private final ProfileService profileService;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createNewUser(UserRequest userRequest) {
        var exists = userRepository.findByEmail(userRequest.email());
        if (exists != null) {
            throw new UsersException("Já existe um e-mail cadastrado, gostaria de efetuar login?");
        }
        String passHashed = BcryptUtil.bcryptHash(userRequest.password());
        var userEntity = UserEntity.builder()
                .email(userRequest.email())
                .passwordHash(passHashed)
                .createdAt(LocalDate.now())
                .build();
        var profile = createProfile(userEntity, userRequest);
        userRepository.persistAndFlush(userEntity);
        profileService.saveProfileAndReturn(profile);

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
    public List<JsonNode> jsonTable(String tableName, String schemaName, int page, int size) {
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid table name");
        }

        if (!schemaName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid schema name");
        }

        EntityManager em = this.userRepository.getEntityManager();
        page = page - 1;
        int offset = page * size;

        // ───────────────────────────────────────────────
        // 2. SQL nativo usando row_to_json + paginação
        // ───────────────────────────────────────────────
        String sql = """
                SELECT row_to_json(t)
                FROM (
                    SELECT *
                    FROM %s.%s
                    LIMIT :size OFFSET :offset
                ) t
                """.formatted(schemaName, tableName);

        // ───────────────────────────────────────────────
        // 3. Execução da query e conversão para JsonNode
        // ───────────────────────────────────────────────
        List<String> rawJsonList = em.createNativeQuery(sql)
                .setParameter("size", size)
                .setParameter("offset", offset)
                .getResultList();

        ObjectMapper mapper = new ObjectMapper();

        return rawJsonList.stream()
                .map(json -> {
                    try {
                        return mapper.readTree(json);
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid JSON from database", e);
                    }
                })
                .toList();
    }

    private ProfileEntity createProfile(UserEntity user, UserRequest userRequest) {
        String fullName = userRequest.name().trim().replaceAll("\\s+", " ");
        String[] parts = fullName.split(" ");
        String name = parts[0];
        String lastName = parts.length > 1
                ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length))
                : "";
        var profile = ProfileEntity.create(user);
        profile.setName(name);
        profile.setLastName(lastName);
        return profile;
    }

    @Override
    public String getTokenUser(String email, String password) {
        return authService.getTokenUser(email, password);
    }
}
