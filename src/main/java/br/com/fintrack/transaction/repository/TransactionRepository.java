package br.com.fintrack.transaction.repository;

import br.com.fintrack.transaction.domain.TransactionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TransactionRepository implements PanacheRepositoryBase<TransactionEntity, UUID> {

    public List<TransactionEntity> findUnfinishedInstallments() {
        return list("installment = ?1 and installmentNumber < installmentTotal and generated=false" , true);
    }
}
