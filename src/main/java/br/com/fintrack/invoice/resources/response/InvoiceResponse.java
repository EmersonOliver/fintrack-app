package br.com.fintrack.invoice.resources.response;

import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.transaction.resources.response.TransactionResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceResponse(Long invoiceId,
                              LocalDate periodStart, LocalDate periodEnd,
                              Integer referenceYear, Integer referenceMonth,
                              BigDecimal totalAmount, InvoiceStatus status) {

    public static InvoiceResponse fromEntity(InvoiceEntity invoice) {
        return new InvoiceResponse(invoice.getInvoiceId(),
                invoice.getPeriodStart(),
                invoice.getPeriodEnd(),
                invoice.getReferenceYear(),
                invoice.getReferenceMonth(),
                invoice.getTotalAmount(),
                invoice.getStatus());
    }
}
