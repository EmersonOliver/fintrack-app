package br.com.fintrack.job;


import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.invoice.repository.InvoiceRepository;
import br.com.fintrack.invoice.service.InvoiceService;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.service.TransactionService;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class InvoiceOpeningJob {

    private final CardService cardService;
    private final InvoiceService invoiceService;
    private final TransactionService transactionService;

    @Transactional
    @Scheduled(cron = "0/10 * * * * ?") // todo dia 00:01
    public void ensureOpenInvoices() {

        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        cardService.listAll().forEach(card -> {
            invoiceService.getOrCreateInvoice(card, currentMonth);
        });
        log.info("Faturas abertas garantidas para o mês {}", currentMonth);
    }

//    void onStart(@Observes StartupEvent ev) {
//        log.info("Iniciando processamento inicial de faturas...");
//        openNewInvoices();
//    }
//
//    @Transactional
//    @Scheduled(cron = "0 2 23 * * ?")
//    public void openNewInvoices() {
//        log.info("Abrindo novas faturas...");
//        List<CardEntity> cards = cardService.listAll();
//        for (CardEntity card : cards) {
//            LocalDate firstDate = LocalDate.now().withDayOfMonth(card.getClosingDate());
//            LocalDate firstNext = LocalDate.now().plusMonths(1L).withDayOfMonth(card.getDueDate());
//
//            InvoiceEntity invoiceEntity = invoiceRepository.find("card.cardId = ?1 and periodStart =?2 and periodEnd = ?3 and status =?4", card.getCardId(), firstDate, firstNext, InvoiceStatus.OPEN)
//                    .firstResultOptional().orElse(null);
//            if (invoiceEntity != null) {
//                continue;
//            }else {
//                createNewInvoice(card);
//            }
//            if (LocalDate.now().compareTo(LocalDate.now().withDayOfMonth(card.getClosingDate())) > 0L) {
//                createNewInvoice(card);
//            }
//        }
//    }
//
//    private void createNewInvoice(CardEntity card) {
//        var newInvoice = InvoiceEntity.builder()
//                .periodStart(LocalDate.now().withDayOfMonth(card.getClosingDate()))
//                .periodEnd(LocalDate.now().plusMonths(1).withDayOfMonth(card.getDueDate()))
//                .status(InvoiceStatus.OPEN)
//                .totalAmount(BigDecimal.ZERO)
//                .createdAt(LocalDate.now())
//                .card(card)
//                .build();
//
//        invoiceRepository.persist(newInvoice);
//        putTransactionsInvoice(newInvoice, card);
//        log.info("Nova fatura criada para o cartão {}", card.getLastDigits());
//    }
//
//    private void putTransactionsInvoice(InvoiceEntity invoiceEntity, CardEntity card) {
//        LocalDate firstDate = LocalDate.now().withDayOfMonth(card.getClosingDate());
//        LocalDate firstNext = LocalDate.now().plusMonths(1L).withDayOfMonth(card.getDueDate());
//        List<TransactionEntity> transactions = transactionService
//                .loadTransactionsByCard(invoiceEntity.getCard().getOwner().getUserId(),
//                        invoiceEntity.getCard().getCardId(), firstDate, firstNext);
//        BigDecimal totalAmount =
//                transactions.stream().map(TransactionEntity::getInstallmentValue)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//        for(TransactionEntity tr : transactions){
//            tr.setInvoice(invoiceEntity);
//            transactionService.update(tr);
//        }
//        invoiceEntity.setTotalAmount(totalAmount);
//        this.invoiceRepository.persistAndFlush(invoiceEntity);
//    }
}
