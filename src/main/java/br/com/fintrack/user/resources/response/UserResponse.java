package br.com.fintrack.user.resources.response;

import br.com.fintrack.card.resources.response.CardResponse;
import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.wallet.resources.response.WalletResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(UUID userId,
                           String name,
                           String email,
                           LocalDate createdAt,
                           List<WalletResponse> wallets,
                           List<CardResponse> cards) {

    public static UserResponse fromEntity(UserEntity entity) {
        if (entity == null) return null;

        return new UserResponse(
                entity.getUserId(),
                entity.getName(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getWallets() != null && !entity.getWallets().isEmpty() ?
                        entity.getWallets().stream().map(WalletResponse::fromEntity).toList() : List.of(),
                entity.getCards() != null && !entity.getCards().isEmpty() ?
                        entity.getCards().stream().map(CardResponse::fromEntity).toList() : List.of()

        );
    }
}
