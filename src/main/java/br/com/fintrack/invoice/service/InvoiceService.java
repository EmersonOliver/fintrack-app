package br.com.fintrack.invoice.service;

import br.com.fintrack.invoice.resources.request.InvoiceRequest;
import br.com.fintrack.invoice.resources.response.InvoiceResponse;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {
    void saveInvoice(InvoiceRequest request, UUID cardId);
    List<InvoiceResponse> findAllInvoiceByCardIdWithResponse(String cardId);
    InvoiceResponse updatedInvoice(Long invoiceId, InvoiceRequest request);
}
