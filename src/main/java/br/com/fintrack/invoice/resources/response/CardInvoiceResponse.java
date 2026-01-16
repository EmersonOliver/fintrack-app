package br.com.fintrack.invoice.resources.response;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.resources.response.CardResponse;
import br.com.fintrack.common.enums.CardType;
import br.com.fintrack.wallet.resources.response.WalletResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CardInvoiceResponse(UUID cardId,
                                  String cardName,
                                  String brandCard,
                                  String lastDigits,
                                  BigDecimal limitAvailable,
                                  BigDecimal limitUsed,
                                  BigDecimal limitTotal,
                                  Integer dueDate,
                                  Integer closingDate,
                                  Boolean active,
                                  CardType cardType,
                                  UUID parentCardId,
                                  List<CardResponse> virtualCards
) {
    public static CardInvoiceResponse fromEntity(CardEntity entity) {
        return new CardInvoiceResponse(
                entity.getCardId(),
                entity.getCardName(),
                entity.getBrandCard() != null ? entity.getBrandCard().name() : null,
                entity.getLastDigits(),
                entity.getLimitAvailable(),
                entity.getLimitUsed(),
                entity.getLimitTotal(),
                entity.getDueDate(),
                entity.getClosingDate(),
                entity.getActive(),
                entity.getCardType(),
                entity.getParentCard() != null ? entity.getParentCard().getCardId() : null,
                entity.getVirtualCards() != null && !entity.getVirtualCards().isEmpty() ?
                        entity.getVirtualCards().stream().map(CardResponse::fromEntity).toList() : List.of()

        );
    }
}
