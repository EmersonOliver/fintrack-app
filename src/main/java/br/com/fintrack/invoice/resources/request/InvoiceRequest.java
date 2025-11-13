package br.com.fintrack.invoice.resources.request;

import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.common.utils.JsonUtils;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceRequest(LocalDate periodStart,
                             LocalDate periodEnd,
                             BigDecimal totalAmount,
                             InvoiceStatus status) {
    public InvoiceEntity fromEntity() {
        return InvoiceEntity.builder()
                .totalAmount(totalAmount)
                .status(status)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .createdAt(LocalDate.now())
                .build();
    }

    @Override
    public String toString() {
        try {
            return JsonUtils.objectToJson(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
