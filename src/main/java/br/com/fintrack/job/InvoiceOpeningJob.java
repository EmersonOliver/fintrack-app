package br.com.fintrack.job;


import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.invoice.repository.InvoiceRepository;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class InvoiceOpeningJob {

    private final CardService cardService;
    private final InvoiceRepository invoiceRepository;


    void onStart(@Observes StartupEvent ev) {
        log.info("Iniciando processamento inicial de faturas...");
        openNewInvoices();
    }

    @Transactional
    @Scheduled(cron = "0 2 23 * * ?")
    public void openNewInvoices() {
        log.info("Abrindo novas faturas...");
        List<CardEntity> cards = cardService.listAll();
        for (CardEntity card : cards) {
            if (LocalDate.now().compareTo(LocalDate.now().withDayOfMonth(card.getClosingDate())) > 0L) {
                createNewInvoice(card);
            }
        }
    }

    private void createNewInvoice(CardEntity card) {
        var newInvoice = InvoiceEntity.builder()
                .periodStart(LocalDate.now())
                .periodEnd(LocalDate.now().plusMonths(1).withDayOfMonth(card.getDueDate()))
                .status(InvoiceStatus.OPEN)
                .totalAmount(BigDecimal.ZERO)
                .card(card)
                .build();

        invoiceRepository.persist(newInvoice);
        log.info("Nova fatura criada para o cartão {}", card.getLastDigits());
    }
}
