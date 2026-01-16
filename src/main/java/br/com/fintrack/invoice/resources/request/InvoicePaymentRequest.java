package br.com.fintrack.invoice.resources.request;

import br.com.fintrack.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoicePaymentRequest(
        UUID walletSelected,
        String description,
        BigDecimal paidAmount,
        BigDecimal totalAmount,
        String paymentDate,
        PaymentStatus paymentStatus
) {
}
