package br.com.fintrack.wallet.service.impl;

import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.user.service.UserService;
import br.com.fintrack.wallet.domain.WalletEntity;
import br.com.fintrack.wallet.repository.WalletRepository;
import br.com.fintrack.wallet.resources.request.WalletRequest;
import br.com.fintrack.wallet.resources.response.WalletResponse;
import br.com.fintrack.wallet.service.WalletService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;

    @Override
    @Transactional
    public WalletResponse create(WalletRequest request, UUID ownerId) {
        var user = userService.loadById(ownerId);
        if (user != null) {
            WalletEntity entity = WalletEntity.builder()
                    .ownerId(user.getUserId())
                    .walletName(request.walletName())
                    .walletType(request.walletType())
                    .active(request.active() != null ? request.active() : true)
                    .balance(request.balance() != null ? request.balance() : BigDecimal.ZERO)
                    .build();

            walletRepository.persistAndFlush(entity);
            return WalletResponse.fromEntity(entity);
        }
        throw new UsersException("O Usuário não corresponde na base de dados!");
    }

    @Override
    public List<WalletResponse> listByOwner(UUID ownerId) {
        return walletRepository.findByOwnerId(ownerId)
                .stream()
                .map(WalletResponse::fromEntity)
                .toList();
    }

    @Override
    public WalletResponse findById(UUID walletId) {
        return walletRepository.findByIdOptional(walletId)
                .map(WalletResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
    }

    @Transactional
    @Override
    public WalletResponse update(UUID walletId, WalletRequest request) {
        WalletEntity entity = walletRepository.findByIdOptional(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (request.walletName() != null) entity.setWalletName(request.walletName());
        if (request.walletType() != null) entity.setWalletType(request.walletType());
        if (request.active() != null) entity.setActive(request.active());
        if (request.balance() != null) entity.setBalance(request.balance());

        walletRepository.persistAndFlush(entity);
        return WalletResponse.fromEntity(entity);
    }

    @Override
    public Optional<WalletEntity> findEntityById(UUID walletId) {
       return walletRepository.findByIdOptional(walletId);
    }

    @Transactional
    @Override
    public void delete(UUID walletId) {
        if (walletRepository.findByIdOptional(walletId).isPresent()) {
            throw new IllegalArgumentException("Wallet not found");
        }
        walletRepository.deleteById(walletId);
    }
}
