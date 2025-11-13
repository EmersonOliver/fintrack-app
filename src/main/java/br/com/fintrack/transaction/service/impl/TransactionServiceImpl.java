package br.com.fintrack.transaction.service.impl;

import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.repository.TransactionRepository;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.resources.response.TransactionResponse;
import br.com.fintrack.transaction.service.TransactionService;
import br.com.fintrack.wallet.service.WalletService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final CardService cardService;
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        var card = cardService.loadCardById(request.cardId())
                .orElseThrow(() -> new UsersException("Cartao não encontrado"));
        var wallet = walletService.findEntityById(UUID.fromString(request.walletId()))
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        BigDecimal totalAmount = request.amount();
        int totalInstallments =request.installmentTotal() != null ? request.installmentTotal() : 1;
        BigDecimal installmentValue = totalAmount.divide(BigDecimal.valueOf(totalInstallments), 2, RoundingMode.HALF_UP);

        var transaction = TransactionEntity.builder()
                .description(request.description())
                .amount(request.amount())
                .installmentValue(installmentValue)
                .date(LocalDate.parse(request.date()))
                .type(request.type())
                .generated(!(totalInstallments > 1))
                .method(request.method())
                .installmentTotal(totalInstallments)
                .installment(totalInstallments > 1)
                .installmentNumber(request.installmentNumber())
                .installmentTotal(request.installmentTotal())
                .card(card)
                .wallet(wallet)
                .build();

        transactionRepository.persist(transaction);
        return TransactionResponse.fromEntity(transaction);
    }
}
