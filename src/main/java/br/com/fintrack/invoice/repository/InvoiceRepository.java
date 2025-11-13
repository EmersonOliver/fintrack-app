package br.com.fintrack.invoice.repository;

import br.com.fintrack.invoice.domain.InvoiceEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InvoiceRepository implements PanacheRepositoryBase<InvoiceEntity, Long> {
}
