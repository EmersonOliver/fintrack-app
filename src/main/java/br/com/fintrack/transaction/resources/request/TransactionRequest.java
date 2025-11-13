package br.com.fintrack.transaction.resources.request;

import br.com.fintrack.common.enums.PaymentMethod;
import br.com.fintrack.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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
}
