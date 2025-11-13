package br.com.fintrack.wallet.repository;

import br.com.fintrack.wallet.domain.WalletEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WalletRepository implements PanacheRepositoryBase<WalletEntity, UUID> {

    public List<WalletEntity> findByOwnerId(UUID ownerId) {
        return find("where ownerId =:ownerId", Parameters.with("ownerId", ownerId))
                .list();
    }
}
