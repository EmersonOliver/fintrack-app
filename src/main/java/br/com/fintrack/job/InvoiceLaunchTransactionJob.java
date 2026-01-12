//package br.com.fintrack.job;
//
//import br.com.fintrack.invoice.domain.InvoiceEntity;
//import br.com.fintrack.invoice.service.InvoiceService;
//import br.com.fintrack.transaction.repository.TransactionRepository;
//import io.quarkus.scheduler.Scheduled;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import java.math.BigDecimal;
//
//@Slf4j
//@ApplicationScoped
//@RequiredArgsConstructor
//public class InvoiceLaunchTransactionJob {
//
//    private final TransactionRepository transactionRepository;
//    private final InvoiceService invoiceService;
//
//    @Transactional
//    @Scheduled(cron = "0/10 * * * * ?")
//    public void start() {
//        var listTransaction = transactionRepository.findUnfinishedInstallments();
//
//    }
//}
