package br.com.fintrack.transaction.resources.response;

import br.com.fintrack.card.resources.response.CardResponse;
import br.com.fintrack.invoice.resources.response.InvoiceResponse;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.wallet.resources.response.WalletResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(UUID transactionId,
                                  String description,
                                  BigDecimal amount,
                                  BigDecimal installmentValue,
                                  LocalDate date,
                                  String type,
                                  String method,
                                  Boolean installment,
                                  Integer installmentNumber,
                                  Integer installmentTotal,
                                  CardResponse card,
                                  InvoiceResponse invoice,
                                  WalletResponse wallet) {
    public static TransactionResponse fromEntity(TransactionEntity entity) {
        if (entity == null) return null;

        return new TransactionResponse(
                entity.getTransactionId(),
                entity.getDescription(),
                entity.getAmount(),
                entity.getInstallmentValue(),
                entity.getDate(),
                entity.getType() != null ? entity.getType().name() : null,
                entity.getMethod() != null ? entity.getMethod().name() : null,
                entity.getInstallment(),
                entity.getInstallmentNumber(),
                entity.getInstallmentTotal(),
                entity.getCard() != null ? CardResponse.fromEntity(entity.getCard()) : null,
                entity.getInvoice() != null ? InvoiceResponse.fromEntity(entity.getInvoice()) : null,
                entity.getWallet() != null ? WalletResponse.fromEntity(entity.getWallet()) : null
        );
    }
}
