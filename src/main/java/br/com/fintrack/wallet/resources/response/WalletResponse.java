package br.com.fintrack.wallet.resources.response;

import br.com.fintrack.common.enums.WalletType;
import br.com.fintrack.wallet.domain.WalletEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse(
        UUID walletId,
        UUID ownerId,
        String walletName,
        WalletType walletType,
        Boolean active,
        BigDecimal balance
) {
    public static WalletResponse fromEntity(WalletEntity entity) {
        return new WalletResponse(
                entity.getWalletId(),
                entity.getOwnerId(),
                entity.getWalletName(),
                entity.getWalletType(),
                entity.getActive(),
                entity.getBalance()
        );
    }
}
