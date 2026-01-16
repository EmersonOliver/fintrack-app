package br.com.fintrack.user.service;

import br.com.fintrack.user.domain.ProfileEntity;
import br.com.fintrack.user.resources.request.ProfileUpdateRequest;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public interface ProfileService {
    void updateProfile(ProfileUpdateRequest data, FileUpload photo);
    ProfileEntity saveProfileAndReturn(ProfileEntity profile);
}
