package br.com.fintrack.job;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.invoice.service.InvoiceService;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.repository.TransactionRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class TransactionInstallmentJob {

    private final TransactionRepository transactionRepository;
    private final InvoiceService invoiceService;

    @Transactional
    @Scheduled(cron = "1 * * * * ?")
    public void generateInstallments() {

        List<TransactionEntity> baseTransactions =
                transactionRepository.findUnfinishedInstallments();

        baseTransactions.forEach(this::generateInstallments);

        log.info("Processamento de parcelas concluído");
    }

    private void generateInstallments(TransactionEntity base) {

        CardEntity card = base.getCard();
        LocalDate purchaseDate = base.getDate();

        YearMonth firstInvoiceMonth =
                resolveInvoiceMonth(purchaseDate, card.getClosingDate());

        for (int i = 1; i <= base.getInstallmentTotal(); i++) {

            YearMonth installmentMonth = firstInvoiceMonth.plusMonths(i - 1);

            InvoiceEntity invoice =
                    invoiceService.getOrCreateInvoice(card, installmentMonth);

            TransactionEntity installment = TransactionEntity.builder()
                    .description(base.getDescription()
                            + " (" + i + "/" + base.getInstallmentTotal() + ")")
                    .amount(base.getAmount())
                    .installmentValue(base.getInstallmentValue())
                    .date(invoice.getPeriodEnd())
                    .type(base.getType())
                    .method(base.getMethod())
                    .installment(true)
                    .generated(true)
                    .installmentNumber(i)
                    .installmentTotal(base.getInstallmentTotal())
                    .card(card)
                    .wallet(base.getWallet())
                    .invoice(invoice)
                    .userTransaction(base.getUserTransaction())
                    .build();

            transactionRepository.persist(installment);
        }

        base.setGenerated(true);
        transactionRepository.persist(base);

        log.info("Parcelas geradas para transação {}", base.getTransactionId());
    }

    private YearMonth resolveInvoiceMonth(LocalDate purchaseDate, int closingDay) {

        LocalDate closingDate = purchaseDate.withDayOfMonth(
                Math.min(closingDay, purchaseDate.lengthOfMonth())
        );

        return purchaseDate.isAfter(closingDate)
                ? YearMonth.from(purchaseDate.plusMonths(1))
                : YearMonth.from(purchaseDate);
    }
}
