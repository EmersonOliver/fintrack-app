package br.com.fintrack.job;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.invoice.repository.InvoiceRepository;
import br.com.fintrack.transaction.domain.TransactionEntity;
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

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class InvoiceJob {

    private final CardService cardService;
    private final InvoiceRepository invoiceRepository;


    void onStart(@Observes StartupEvent ev) {
        log.info("Iniciando processamento inicial de faturas...");
        processInvoice();
    }

    @Transactional
    @Scheduled(cron = "0 0 23 * * ?")
    public void processInvoice() {
        log.info("Executando faturas");
        List<CardEntity> cards = cardService.listAll();
        for (CardEntity card : cards) {
            if (LocalDate.now().getDayOfMonth() == card.getClosingDate()) {
                log.info("Fechando fatura do cartão {}", card.getLastDigits());
                closeInvoice(card);
            }
        }
    }


    private void closeInvoice(CardEntity card) {
        List<TransactionEntity> transactions = card.getTransactions().stream()
                .filter(t -> t.getDate().getMonth().equals(LocalDate.now().getMonth()))
                .toList();

        BigDecimal totalAmount = transactions.stream()
                .map(TransactionEntity::getInstallmentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var openInvoice = card.getInvoices().stream()
                .filter(i -> i.getStatus() == InvoiceStatus.OPEN)
                .findFirst()
                .orElse(null);

        if (openInvoice != null) {
            openInvoice.setStatus(InvoiceStatus.CLOSED);
            openInvoice.setTotalAmount(totalAmount);
            openInvoice.setPeriodEnd(LocalDate.now());
            invoiceRepository.persist(openInvoice);
        } else {
            log.warn("Nenhuma fatura aberta encontrada para cartão {}", card.getLastDigits());
        }
    }
}
