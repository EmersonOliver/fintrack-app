package br.com.fintrack.job;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.repository.TransactionRepository;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@ApplicationScoped
public class TransactionJob {

    @Inject
    TransactionRepository transactionRepository;

    void onStart(@Observes StartupEvent ev) {
        log.info("Iniciando job de lancamento de transacoes parceladas...");
        processInstallments();
    }

    @Transactional
//    @Scheduled(cron = "0 0 6 * * ?") // roda todos os dias às 06h
    @Scheduled(cron = "1 * * * * ?") // roda todos os dias
    public void processInstallments() {
        log.info("Executando job de geração de parcelas de transacoes...");

        List<TransactionEntity> transactions = transactionRepository.findUnfinishedInstallments();
        for (TransactionEntity transaction : transactions) {
            generateNextInstallment(transaction);
        }
    }


    String description;
    private void generateNextInstallment(TransactionEntity baseTransaction) {

        IntStream.range(1, baseTransaction.getInstallmentTotal()+1).forEach(i->{
            if (i == 1) {
                description = baseTransaction.getDescription();
                return;
            }
            TransactionEntity newTransaction = TransactionEntity.builder()
                    .description(description + " (Parcela " + i + "/" + baseTransaction.getInstallmentTotal() + ")")
                    .amount(baseTransaction.getAmount())
                    .date(baseTransaction.getDate().plusMonths(i-1))
                    .type(baseTransaction.getType())
                    .method(baseTransaction.getMethod())
                    .installmentValue(baseTransaction.getInstallmentValue())
                    .installment(true)
                    .generated(true)
                    .installmentNumber(i)
                    .userTransaction(baseTransaction.getUserTransaction())
                    .wallet(baseTransaction.getWallet())
                    .card(baseTransaction.getCard())
                    .installmentTotal(baseTransaction.getInstallmentTotal())
                    .build();
            baseTransaction.setGenerated(true);
            transactionRepository.persist(newTransaction);
            transactionRepository.persist(baseTransaction);

            log.info("Gerada nova parcela {}/{} da transação {}",
                    i,
                    baseTransaction.getInstallmentTotal(),
                    baseTransaction.getTransactionId());
        });
        log.info("Todas as parcelas ja foram geradas para a transacao {}", baseTransaction.getTransactionId());
    }
}
