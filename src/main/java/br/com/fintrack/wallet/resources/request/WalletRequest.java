package br.com.fintrack.wallet.resources.request;

import br.com.fintrack.common.enums.WalletType;

import java.math.BigDecimal;

public record WalletRequest(String ownerId, String walletName,
                            WalletType walletType, Boolean active,
                            BigDecimal balance) {
}
