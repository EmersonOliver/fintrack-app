package br.com.fintrack.card.resources.request;

import br.com.fintrack.common.enums.BrandCard;
import br.com.fintrack.common.enums.CardType;
import br.com.fintrack.common.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;

public record CardRequest(String cardName, Integer dueDate, Integer closingDate,
                          BrandCard brandCard, String lastDigits,
                          BigDecimal limitTotal, BigDecimal limitUsed, String parentCard,
                          BigDecimal limitAvailable, Boolean active, CardType cardType) {

    @Override
    public String toString() {
        try {
            return JsonUtils.objectToJson(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
