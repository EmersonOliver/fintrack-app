package br.com.fintrack.invoice.resources.response;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.transaction.domain.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SummaryInvoice(LocalDate startDate, LocalDate endDate,
                             String cardName,
                             String lastDigitsCard,
                             Integer closingDate,
                             Integer dueDate,
                             BigDecimal limitAvailable,
                             BigDecimal limitUsed,
                             BigDecimal value) {

    public static SummaryInvoice summaryInvoiceResponse(CardEntity card, List<TransactionEntity> transactions, LocalDate referenceDate) {
        LocalDate endDate = referenceDate.plusMonths(1L);
        BigDecimal invoiceValue = transactions.stream().map(TransactionEntity::getInstallmentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SummaryInvoice(referenceDate, endDate, card.getCardName(), card.getLastDigits(), card.getClosingDate(),
                card.getDueDate(),
                card.getLimitAvailable(),
                card.getLimitUsed(),
                invoiceValue);
    }
}
