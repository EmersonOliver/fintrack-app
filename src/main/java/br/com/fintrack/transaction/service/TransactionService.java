package br.com.fintrack.transaction.service;

import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.resources.response.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse create(TransactionRequest request);
    TransactionResponse loadByTransactionId(UUID transactionId, UUID userId);
    List<TransactionResponse> loadAllTransactions(UUID userId);
    TransactionResponse updateTransaction(UUID transactionId, UUID userId, TransactionRequest request);

}
