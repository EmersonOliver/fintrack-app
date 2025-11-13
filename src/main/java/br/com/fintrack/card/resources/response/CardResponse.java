package br.com.fintrack.card.resources.response;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.common.enums.CardType;
import br.com.fintrack.invoice.resources.response.InvoiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CardResponse(UUID cardId,
                           String cardName,
                           String brandCard,
                           String lastDigits,
                           BigDecimal limitAvailable,
                           BigDecimal limitUsed,
                           BigDecimal limitTotal,
                           Boolean active,
                           CardType cardType,
                           UUID parentCardId,
                           List<CardResponse> virtualCards,
                           @JsonIgnoreProperties("card")
                           List<InvoiceResponse> invoices
) {
    public static CardResponse fromEntity(CardEntity entity) {
        return new CardResponse(
                entity.getCardId(),
                entity.getCardName(),
                entity.getBrandCard() != null ? entity.getBrandCard().name() : null,
                entity.getLastDigits(),
                entity.getLimitAvailable(),
                entity.getLimitUsed(),
                entity.getLimitTotal(),
                entity.getActive(),
                entity.getCardType(),
                entity.getParentCard() != null ? entity.getParentCard().getCardId() : null,
                entity.getVirtualCards() != null && !entity.getVirtualCards().isEmpty() ?
                        entity.getVirtualCards().stream().map(CardResponse::fromEntity).toList() : List.of(),
                entity.getInvoices() != null && !entity.getInvoices().isEmpty() ?
                        entity.getInvoices().stream().map(InvoiceResponse::fromEntity).toList() : List.of()
        );
    }

}
