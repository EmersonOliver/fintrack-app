package br.com.fintrack.card.resources.request;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.common.enums.BrandCard;
import br.com.fintrack.common.enums.CardType;

import javax.smartcardio.Card;
import java.math.BigDecimal;
import java.util.UUID;

public record CardRequest(String cardName, Integer dueDate, Integer closingDate,
                          BrandCard brandCard, String lastDigits,
                          BigDecimal limitTotal, BigDecimal limitUsed, String parentCard,
                          BigDecimal limitAvailable, Boolean active, CardType cardType) {
}
