package br.com.fintrack.invoice.resources.response;

import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.invoice.domain.InvoiceEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponse(Long invoiceId,
                              LocalDate periodStart, LocalDate periodEnd,
                              BigDecimal totalAmount, InvoiceStatus status) {

    public static InvoiceResponse fromEntity(InvoiceEntity invoice) {
        return new InvoiceResponse(invoice.getInvoiceId(),
                invoice.getPeriodStart(),
                invoice.getPeriodEnd(),
                invoice.getTotalAmount(),
                invoice.getStatus());
    }
}
