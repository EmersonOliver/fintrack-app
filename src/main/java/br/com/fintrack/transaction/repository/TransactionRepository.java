package br.com.fintrack.transaction.repository;

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

    public List<TransactionEntity> findByUserIdAndCardId(UUID userId, UUID cardId, LocalDate startDate, LocalDate endDate) {
        return find("userTransaction.userId = :userId AND card.cardId = :cardId and date >=:startDate and date < :endDate",
                Parameters.with("userId", userId)
                        .and("cardId", cardId).and("startDate", startDate).and("endDate", endDate))
                .list();
    }


}
