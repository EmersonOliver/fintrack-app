package br.com.fintrack.user.service.impl;

import br.com.fintrack.core.security.AuthSecurityContext;
import br.com.fintrack.user.domain.ProfileEntity;
import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.user.repository.ProfileRepository;
import br.com.fintrack.user.resources.request.ProfileUpdateRequest;
import br.com.fintrack.user.service.ProfileService;
import br.com.fintrack.user.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final Path UPLOAD_DIR =
            Path.of("uploads/profiles");
    private final UserService userService;
    private final AuthSecurityContext securityContext;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public void updateProfile(ProfileUpdateRequest data, FileUpload photo) {
        UUID userId = securityContext.getInstance().get().userId;
        UserEntity user = userService.loadById(userId);

        ProfileEntity profile =
                profileRepository.findByUser(user)
                        .orElseGet(() -> ProfileEntity.create(user));

        profile.update(data);

        if (photo != null && photo.size() > 0) {
            String path = savePhoto(user.getUserId(), photo);
            profile.setPhotoProfilePath(path);
        }
        profileRepository.persist(profile);
    }

    @Override
    public ProfileEntity saveProfileAndReturn(ProfileEntity profile) {
        this.profileRepository.persistAndFlush(profile);
        return profile;
    }

    private String savePhoto(UUID userId, FileUpload photo) {
        try {
            Files.createDirectories(UPLOAD_DIR);

            String filename = "profile-" + userId + ".jpg";
            Path target = UPLOAD_DIR.resolve(filename);

            Files.copy(photo.uploadedFile(), target,
                    StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/profiles/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar imagem", e);
        }
    }
}
