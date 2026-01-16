package br.com.fintrack.invoice.service;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.invoice.resources.request.InvoicePaymentRequest;
import br.com.fintrack.invoice.resources.request.InvoiceRequest;
import br.com.fintrack.invoice.resources.response.InvoiceResponse;
import br.com.fintrack.invoice.resources.response.SummaryInvoice;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.resources.response.TransactionResponse;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface InvoiceService {
    void saveInvoice(InvoiceRequest request, UUID cardId);

    InvoiceResponse getInvoiceById(Long invoiceId);

    InvoiceEntity getInvoiceEntityById(Long invoiceId);

    InvoiceEntity getOrCreateInvoice(CardEntity card, YearMonth referenceMonth);

    void updateInvoiceEntity(InvoiceEntity invoiceEntity);

    List<InvoiceResponse> findAllInvoiceByCardIdWithResponse(String cardId);

    InvoiceResponse updatedInvoice(Long invoiceId, InvoiceRequest request);

    void applyInvoicePayment(Long invoiceId, TransactionRequest request);

    List<SummaryInvoice> getResumeInvoice(String referenceDate, UUID userId);

    void recalculateInvoice(InvoiceEntity invoice);
}
