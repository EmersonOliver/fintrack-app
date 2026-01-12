package br.com.fintrack.user.resources.response;

import br.com.fintrack.card.resources.response.CardResponse;
import br.com.fintrack.common.enums.CardType;
import br.com.fintrack.common.enums.PaymentMethod;
import br.com.fintrack.common.enums.TransactionType;
import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.wallet.domain.WalletEntity;
import br.com.fintrack.wallet.resources.response.WalletResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public record UserProfileResponse(
        String name,
        String email,
        BigDecimal walletAmount,
        BigDecimal monthlyNetBalance,
        BigDecimal creditCycleSpending,
        List<CardResponse> cards,
        List<WalletResponse> wallets) {

    public static UserProfileResponse mapProfileEntity(UserEntity userEntity) {
        BigDecimal walletAmount =
                userEntity.getWallets().stream()
                        .map(WalletEntity::getBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyNetBalance =
                userEntity.getWallets().stream()
                        .flatMap(w -> w.getTransactions().stream())
                        .filter(tr -> isInCurrentMonth(tr.getDate()))
                        .map(tr -> tr.getType().equals(TransactionType.INCOME)
                                ? tr.getAmount()
                                : tr.getAmount().negate()
                        )
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditCycleSpending =
                userEntity.getCards().stream()
                        .filter(card -> card.getCardType().equals(CardType.CREDIT))
                        .flatMap(card -> {
                            LocalDate end = calculateEndDate(card.getClosingDate(), LocalDate.now());
                            LocalDate start = calculateStartDate(card.getClosingDate(), end);

                            return card.getTransactions().stream()
                                    .filter(tr ->
                                            !tr.getDate().isBefore(start) &&
                                                    tr.getDate().isBefore(end) &&
                                                    tr.getType().equals(TransactionType.EXPENSE) &&
                                                    tr.getMethod().equals(PaymentMethod.CREDIT)
                                    )
                                    .map(tr -> tr.getInstallment() != null && tr.getInstallment()
                                            ? tr.getInstallmentValue()
                                            : tr.getAmount()
                                    );
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new UserProfileResponse(
                userEntity.getName(),
                userEntity.getEmail(),
                walletAmount,
                monthlyNetBalance,
                creditCycleSpending,
                userEntity.getCards().stream().map(CardResponse::fromEntity).toList(),
                userEntity.getWallets().stream().map(WalletResponse::fromEntity).toList()
        );
    }

    private static boolean isInCurrentMonth(LocalDate date) {
        YearMonth now = YearMonth.now();
        return YearMonth.from(date).equals(now);
    }

    public static LocalDate calculateEndDate(int closingDay, LocalDate today) {
        int safeDay = Math.min(closingDay, today.lengthOfMonth());
        return LocalDate.of(
                today.getYear(),
                today.getMonth(),
                safeDay
        );
    }

    public static LocalDate calculateStartDate(int closingDay, LocalDate today) {
        LocalDate previousMonth = today.minusMonths(1);

        int safeDay = Math.min(closingDay, previousMonth.lengthOfMonth());

        return LocalDate.of(
                previousMonth.getYear(),
                previousMonth.getMonth(),
                safeDay
        );
    }

}
