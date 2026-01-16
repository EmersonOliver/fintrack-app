package br.com.fintrack.transaction.repository;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.transaction.domain.TransactionEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TransactionRepository implements PanacheRepositoryBase<TransactionEntity, UUID> {

    public List<TransactionEntity> findUnfinishedInstallments() {
        return list("installment = ?1 and installmentNumber < installmentTotal and generated=false", true);
    }

    public PanacheQuery<TransactionEntity> findByUserId(UUID userId) {
        return find("userTransaction.userId = ?1 ORDER BY date ASC", userId);
    }

    public PanacheQuery<TransactionEntity> findByUserIdAndCardId(UUID userId, UUID cardId, LocalDate startDate, LocalDate endDate) {
        return find("userTransaction.userId = :userId AND card.cardId = :cardId and date >=:startDate and date < :endDate ",
                Parameters.with("userId", userId)
                        .and("cardId", cardId)
                        .and("startDate", startDate)
                        .and("endDate", endDate));
    }

    public List<TransactionEntity> findOpenInstallmentsByCard(CardEntity card) {
        return find("cardId=?", card.getCardId()).list();
    }

    public List<TransactionEntity> findByInvoice(InvoiceEntity invoice) {
        return find("invoice.invoiceId=?1", invoice.getInvoiceId()).list();
    }
}
