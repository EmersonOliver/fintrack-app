package br.com.fintrack.transaction.service;

import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.resources.response.TransactionResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse create(TransactionRequest request);

    TransactionResponse loadByTransactionId(UUID transactionId, UUID userId);

    List<TransactionResponse> loadAllTransactions(UUID userId);

    TransactionResponse updateTransaction(UUID transactionId, UUID userId, TransactionRequest request);

    List<TransactionEntity> loadTransactionsByCard(UUID userId, UUID cardId, LocalDate startDate, LocalDate endDate);

    void update(TransactionEntity tr);
}
