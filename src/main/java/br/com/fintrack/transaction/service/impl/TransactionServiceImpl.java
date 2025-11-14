package br.com.fintrack.transaction.service.impl;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.exceptions.CardNotFoundException;
import br.com.fintrack.common.exceptions.TransactionNotFoundException;
import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.repository.TransactionRepository;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.resources.response.TransactionResponse;
import br.com.fintrack.transaction.service.TransactionService;
import br.com.fintrack.user.service.UserService;
import br.com.fintrack.wallet.domain.WalletEntity;
import br.com.fintrack.wallet.service.WalletService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static br.com.fintrack.common.utils.DataUtils.parseDate;

@ApplicationScoped
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserService userService;
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
        int totalInstallments = request.installmentTotal() != null ? request.installmentTotal() : 1;
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
                .userTransaction(card != null ? card.getOwner() : wallet.getOwner())
                .build();

        transactionRepository.persist(transaction);
        return TransactionResponse.fromEntity(transaction);
    }

    @Override
    public TransactionResponse loadByTransactionId(UUID transactionId, UUID userId) {
        var user = userService.loadById(userId);
        if (Objects.isNull(user))
            throw new UsersException("Usuário não encontrado");
        var transaction = transactionRepository.findByIdOptional(transactionId).orElse(null);
        if (!Objects.isNull(transaction) && transaction.getUserTransaction().getUserId().equals(userId)) {
            return TransactionResponse.fromEntity(transaction);
        } else {
            throw new TransactionNotFoundException("Essa transação não é de origem do usuário!!");
        }
    }

    @Override
    public List<TransactionResponse> loadAllTransactions(UUID userId) {
        var allTransactionsResponse = transactionRepository.findByUserId(userId);
        return allTransactionsResponse.stream().map(TransactionResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(UUID transactionId, UUID userId, TransactionRequest request) {

        var user = userService.loadById(userId);
        if (user == null) {
            throw new UsersException("Usuário inválido");
        }

        var transaction = transactionRepository.findByIdOptional(transactionId)
                .orElseThrow(() -> new UsersException("Transação não encontrada"));

        if (!transaction.getUserTransaction().getUserId().equals(userId)) {
            throw new UsersException("Você não pode atualizar uma transação que não é sua");
        }

        CardEntity card = loadCardIfProvided(request.cardId(), userId);
        WalletEntity wallet = loadWalletIfProvided(request.walletId(), userId);

        LocalDate parsedDate = parseDate(request.date());

        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setDate(parsedDate);
        transaction.setType(request.type());
        transaction.setMethod(request.method());
        transaction.setInstallment(request.installment());
        transaction.setInstallmentNumber(request.installmentNumber());
        transaction.setInstallmentTotal(request.installmentTotal());

        transaction.setCard(card);
        transaction.setWallet(wallet);

        if (Boolean.TRUE.equals(request.installment())) {
            if (request.installmentTotal() == null || request.installmentTotal() <= 1) {
                throw new UsersException("Total de parcelas inválido");
            }

            BigDecimal installmentValue =
                    request.amount().divide(new BigDecimal(request.installmentTotal()), 2, BigDecimal.ROUND_HALF_UP);

            transaction.setInstallmentValue(installmentValue);
        } else {
            transaction.setInstallmentValue(null);
            transaction.setInstallmentNumber(null);
            transaction.setInstallmentTotal(null);
        }

        transactionRepository.persist(transaction);

        return TransactionResponse.fromEntity(transaction);
    }

    private CardEntity loadCardIfProvided(String cardId, UUID userId) {
        if (cardId == null) return null;

        var card = cardService.loadCardById(cardId).orElse(null);
        if (card == null) {
            throw new CardNotFoundException("Cartão não encontrado");
        }

        if (!card.getOwner().getUserId().equals(userId)) {
            throw new CardNotFoundException("O cartão pertence a outro usuário");
        }

        return card;
    }

    private WalletEntity loadWalletIfProvided(String walletId, UUID userId) {
        if (walletId == null) return null;

        UUID uuid;
        try {
            uuid = UUID.fromString(walletId);
        } catch (Exception ex) {
            throw new UsersException("walletId inválido");
        }

        var wallet = walletService.findEntityById(uuid).orElse(null);
        if (wallet == null) {
            throw new UsersException("Carteira não encontrada");
        }

        if (!wallet.getOwner().getUserId().equals(userId)) {
            throw new UsersException("A carteira pertence a outro usuário");
        }

        return wallet;
    }

}
