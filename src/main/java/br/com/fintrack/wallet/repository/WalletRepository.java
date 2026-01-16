package br.com.fintrack.wallet.repository;

import br.com.fintrack.wallet.domain.WalletEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WalletRepository implements PanacheRepositoryBase<WalletEntity, UUID> {

    public PanacheQuery<WalletEntity> findByOwnerId(UUID ownerId) {
        return find("where ownerId =:ownerId order by balance desc", Parameters.with("ownerId", ownerId));
    }
}
