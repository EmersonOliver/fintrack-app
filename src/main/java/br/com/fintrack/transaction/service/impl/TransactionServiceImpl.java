package br.com.fintrack.transaction.service.impl;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.enums.*;
import br.com.fintrack.common.exceptions.CardNotFoundException;
import br.com.fintrack.common.exceptions.TransactionExceedsLimitsException;
import br.com.fintrack.common.exceptions.TransactionNotFoundException;
import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.common.responses.dto.PageResponse;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.repository.TransactionRepository;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.resources.response.TransactionResponse;
import br.com.fintrack.transaction.service.TransactionService;
import br.com.fintrack.user.service.UserService;
import br.com.fintrack.wallet.domain.WalletEntity;
import br.com.fintrack.wallet.resources.request.WalletRequest;
import br.com.fintrack.wallet.service.WalletService;
import io.quarkus.panache.common.Page;
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
        if (request.type() == TransactionType.INCOME) {
            return processIncome(request);
        }
        return processExpense(request);
    }

    private TransactionResponse processIncome(TransactionRequest request) {

        var wallet = walletService.findEntityById(UUID.fromString(request.walletId()))
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        wallet.setBalance(
                wallet.getBalance().add(request.amount())
        );

        walletService.update(wallet.getWalletId(), new WalletRequest(wallet.getWalletName(),
                wallet.getWalletType(),
                wallet.getActive(), wallet.getBalance()));

        TransactionEntity transaction = TransactionEntity.builder()
                .description(request.description())
                .amount(request.amount())
                .date(LocalDate.parse(request.date()))
                .type(TransactionType.INCOME)
                .method(request.method())
                .rootInstallment(Boolean.TRUE)
                .wallet(wallet)
                .userTransaction(wallet.getOwner())
                .status(StatusTransaction.PROCESSED)
                .generated(true)
                .installment(false)
                .build();

        transactionRepository.persist(transaction);

        return TransactionResponse.fromEntity(transaction);
    }

    private TransactionResponse processExpense(TransactionRequest request) {
        if (request.walletId().isEmpty()) {
            throw new IllegalArgumentException("Wallet no selected");
        }
        var wallet = walletService.findEntityById(UUID.fromString(request.walletId()))
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        CardEntity card = null;

        if (request.cardId() != null) {
            card = cardService.loadCardById(request.cardId())
                    .orElseThrow(() -> new UsersException("Cartão não encontrado"));
        }

        if (card != null && card.getCardType() == CardType.CREDIT && request.method().equals(PaymentMethod.CREDIT)) {
            return processCreditExpense(request, card, wallet);
        }
        if (wallet != null && wallet.getActive() && request.method().equals(PaymentMethod.CASH)) {
            BigDecimal walletAmount = wallet.getBalance();
            BigDecimal expenseAmount = request.amount();
            walletAmount = walletAmount.subtract(expenseAmount);

            wallet.setBalance(walletAmount);

            walletService.update(wallet.getWalletId(),
                    new WalletRequest(wallet.getWalletName(), wallet.getWalletType(),
                            wallet.getActive(), walletAmount));
            TransactionEntity transaction = TransactionEntity.builder()
                    .description(request.description())
                    .amount(expenseAmount)
                    .date(LocalDate.parse(request.date()))
                    .type(TransactionType.EXPENSE)
                    .rootInstallment(Boolean.TRUE)
                    .method(request.method())
                    .installmentNumber(1)
                    .card(card)
                    .wallet(wallet)
                    .status(StatusTransaction.PROCESSED)
                    .userTransaction(card.getOwner())
                    .build();

            transactionRepository.persist(transaction);

            return TransactionResponse.fromEntity(transaction);
        }

        return processDebitExpense(request, wallet);
    }

    private TransactionResponse processCreditExpense(
            TransactionRequest request,
            CardEntity card,
            WalletEntity wallet
    ) {
        BigDecimal totalAmount = request.amount();
        int totalInstallments = request.installmentTotal() != null ? request.installmentTotal() : 1;

        if (totalAmount.compareTo(card.getLimitAvailable()) > 0) {
            throw new TransactionExceedsLimitsException(
                    "Transação excede o limite disponível de R$ " + card.getLimitAvailable()
            );
        }

        BigDecimal installmentValue = totalAmount.divide(
                BigDecimal.valueOf(totalInstallments),
                2,
                RoundingMode.HALF_UP
        );

        var invoiceCard = card.getInvoices().stream().filter(status -> status.getStatus()
                .equals(InvoiceStatus.OPEN)).findFirst().orElse(null);

//        if (invoiceCard != null) {
//            BigDecimal invoiceAmount = invoiceCard.getTotalAmount();
//            invoiceAmount = invoiceAmount.add(installmentValue);
//            invoiceCard.setTotalAmount(invoiceAmount);
//        }

        TransactionEntity transaction = TransactionEntity.builder()
                .description(request.description())
                .amount(totalAmount)
                .installmentValue(installmentValue)
                .date(LocalDate.parse(request.date()))
                .type(TransactionType.EXPENSE)
                .method(request.method())
                .installment(totalInstallments > 1)
                .installmentTotal(totalInstallments)
                .installmentNumber(1)
                .rootInstallment(Boolean.TRUE)
                .generated(totalInstallments == 1)
                .status(StatusTransaction.IN_PROCESSING)
                .card(card)
                .wallet(wallet)
                .userTransaction(card.getOwner())
                .invoice(invoiceCard)
                .build();

        transactionRepository.persist(transaction);

        card.setLimitUsed(card.getLimitUsed().add(totalAmount));
        card.updateAvailableLimit();
        cardService.updateLimit(card);

        return TransactionResponse.fromEntity(transaction);
    }

    private TransactionResponse processDebitExpense(
            TransactionRequest request,
            WalletEntity wallet
    ) {
        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalStateException("Saldo insuficiente na carteira");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        walletService.update(wallet.getWalletId(), new WalletRequest(wallet.getWalletName(),
                wallet.getWalletType(), wallet.getActive(), wallet.getBalance()));

        TransactionEntity transaction = TransactionEntity.builder()
                .description(request.description())
                .amount(request.amount())
                .date(LocalDate.parse(request.date()))
                .type(TransactionType.EXPENSE)
                .method(request.method())
                .wallet(wallet)
                .rootInstallment(Boolean.TRUE)
                .userTransaction(wallet.getOwner())
                .status(StatusTransaction.PROCESSED)
                .generated(true)
                .installment(false)
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
    public PageResponse<TransactionResponse> loadAllTransactions(UUID userId, Integer page, Integer size) {
        var query = transactionRepository.findByUserId(userId);
        query.page(Page.of(page, size));
        return new PageResponse<>(query.list().stream().map(TransactionResponse::fromEntity)
                .toList(), query.count(), page, size);
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

        if (request.type() == TransactionType.INCOME) {
            return processIncome(request);
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
                    request.amount().divide(new BigDecimal(request.installmentTotal()), 2,
                            BigDecimal.ROUND_HALF_UP);

            transaction.setInstallmentValue(installmentValue);
        } else {
            transaction.setInstallmentValue(null);
            transaction.setInstallmentNumber(null);
            transaction.setInstallmentTotal(null);
        }

        transactionRepository.persist(transaction);

        return TransactionResponse.fromEntity(transaction);
    }

    @Override
    public PageResponse<TransactionResponse> loadTransactionsByCard(UUID userId, UUID cardId, LocalDate startDate, LocalDate endDate, int page, int size, int referenceMonth, int referenceYear) {
        CardEntity card = cardService.loadCardById(cardId.toString())
                .orElseThrow(() -> new CardNotFoundException("Cartao nao encontrado"));

        var query = transactionRepository.findByUserIdAndCardId(userId, card.getCardId(),
                startDate.withDayOfMonth(card.getClosingDate()).withMonth(referenceMonth).withYear(referenceYear).minusMonths(1),
                endDate.withDayOfMonth(card.getClosingDate()).withMonth(referenceMonth).withYear(referenceYear).plusMonths(1));
        query.page(Page.of(page, size));
        return new PageResponse<>(query.list().stream().filter(res ->
                        res.getInvoice().getReferenceMonth().equals(referenceMonth)
                                && res.getInvoice().getReferenceYear().equals(referenceYear))
                .map(TransactionResponse::fromEntity)
                .toList(), query.count(), page, size);
    }

    @Override
    public List<TransactionEntity> loadTransactionsByCard(UUID userId, UUID cardId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByUserIdAndCardId(userId, cardId, startDate, endDate).list();
    }

    @Override
    @Transactional
    public void update(TransactionEntity tr) {
        this.transactionRepository.persistAndFlush(tr);
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
