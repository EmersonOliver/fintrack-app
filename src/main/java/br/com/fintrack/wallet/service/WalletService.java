package br.com.fintrack.wallet.service;

import br.com.fintrack.wallet.domain.WalletEntity;
import br.com.fintrack.wallet.resources.request.WalletRequest;
import br.com.fintrack.wallet.resources.response.WalletResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletService {

    WalletResponse create(WalletRequest walletRequest, UUID ownerId);
    List<WalletResponse> listByOwner(UUID ownerId);
    WalletResponse findById(UUID walletId);
    WalletResponse update(UUID walletId, WalletRequest request);
    Optional<WalletEntity> findEntityById(UUID walletId);
    void delete(UUID walletId);



}
