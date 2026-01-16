package br.com.fintrack.user.resources.request;

import java.time.LocalDate;

public record ProfileUpdateRequest(
        String name,
        String lastName,
        String bio,
        String phone,
        String country,
        LocalDate birthday
) {
}
