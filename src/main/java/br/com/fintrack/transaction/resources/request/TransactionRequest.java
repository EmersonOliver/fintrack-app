package br.com.fintrack.transaction.resources.request;

import br.com.fintrack.common.enums.PaymentMethod;
import br.com.fintrack.common.enums.TransactionType;
import br.com.fintrack.common.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;

public record TransactionRequest(String description,
                                 BigDecimal amount,
                                 String date,
                                 TransactionType type,
                                 PaymentMethod method,
                                 Boolean installment,
                                 Integer installmentNumber,
                                 Integer installmentTotal,
                                 String cardId,
                                 String walletId) {

    @Override
    public String toString() {
        try {
            return JsonUtils.objectToJson(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
