package br.com.fintrack.transaction.service;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.common.responses.dto.PageResponse;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.invoice.resources.request.InvoicePaymentRequest;
import br.com.fintrack.invoice.resources.request.InvoiceRequest;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.resources.response.TransactionResponse;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse create(TransactionRequest request);
    TransactionEntity persist(TransactionEntity transaction);
    TransactionResponse loadByTransactionId(UUID transactionId, UUID userId);
    PageResponse<TransactionResponse> loadAllTransactions(UUID userId, Integer page, Integer size);
    TransactionResponse updateTransaction(UUID transactionId, UUID userId, TransactionRequest request);
    PageResponse<TransactionResponse> loadTransactionsByCard(UUID userId, UUID cardId, LocalDate startDate, LocalDate endDate, int page, int size, int referenceMonth, int referenceYear);
    List<TransactionEntity> loadTransactionsByCard(UUID userId, UUID cardId, LocalDate startDate, LocalDate endDate);
    void update(TransactionEntity tr);

    void payInvoiceFully(Long invoiceId, TransactionRequest  request);

    void applyPartialPayment(TransactionRequest request);

    List<TransactionEntity> findByInvoiceOrdered(InvoiceEntity invoice);
}
