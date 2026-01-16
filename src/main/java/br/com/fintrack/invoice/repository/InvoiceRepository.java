package br.com.fintrack.invoice.repository;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class InvoiceRepository implements PanacheRepositoryBase<InvoiceEntity, Long> {


    public Optional<InvoiceEntity> findOpenByCardAndReference(UUID cardId, int year, int month) {
        return find("referenceMonth = ?1 and referenceYear =?2 and card.cardId= ?3", month, year, cardId)
                .firstResultOptional();
    }

    public List<InvoiceEntity> findOpenInvoicesAfter(CardEntity card, YearMonth currentPeriod) {
        return List.of();
    }
}
