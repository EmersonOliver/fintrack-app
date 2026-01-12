package br.com.fintrack.job;

import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.invoice.repository.InvoiceRepository;
import br.com.fintrack.transaction.domain.TransactionEntity;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class InvoiceClosingJob {
    private final CardService cardService;
    private final InvoiceRepository invoiceRepository;

    @Transactional
    @Scheduled(cron = "0 0 23 * * ?")
    public void closeInvoices() {

        LocalDate today = LocalDate.now();

        cardService.listAll().forEach(card -> {

            if (today.getDayOfMonth() != card.getClosingDate()) {
                return;
            }

            YearMonth reference = YearMonth.from(today);

            invoiceRepository
                    .findOpenByCardAndReference(card.getCardId(),
                            reference.getYear(),
                            reference.getMonthValue())
                    .ifPresent(invoice -> {

                        BigDecimal total = invoice.getTransactions().stream()
                                .map(TransactionEntity::getInstallmentValue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        invoice.setTotalAmount(total);
                        invoice.setStatus(InvoiceStatus.CLOSED);

                        invoiceRepository.persist(invoice);

                        log.info("Fatura fechada cartão {} mês {} total {}",
                                card.getLastDigits(), reference, total);
                    });
        });
    }
}
