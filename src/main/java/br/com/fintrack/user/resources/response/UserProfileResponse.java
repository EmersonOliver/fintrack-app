package br.com.fintrack.user.resources.response;

import br.com.fintrack.card.resources.response.CardResponse;
import br.com.fintrack.common.enums.CardType;
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
                        .map(tr -> tr.getType() == TransactionType.INCOME
                                ? tr.getAmount()
                                : tr.getAmount().negate()
                        )
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditCycleSpending =
                userEntity.getCards().stream()
                        .filter(card -> card.getCardType() == CardType.CREDIT)
                        .flatMap(card -> {
                            LocalDate start = calculateStartDate(card.getClosingDate(), LocalDate.now());
                            LocalDate end = calculateEndDate(card.getClosingDate(), LocalDate.now());

                            return card.getTransactions().stream()
                                    .filter(tr ->
                                            !tr.getDate().isBefore(start) &&
                                                    tr.getDate().isBefore(end) &&
                                                    tr.getType() == TransactionType.EXPENSE
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

        LocalDate endDate = LocalDate.of(
                today.getYear(),
                today.getMonth(),
                safeDay
        );

        // Se hoje ainda não chegou no fechamento, o fechamento válido é o mês anterior
        if (today.isBefore(endDate)) {
            LocalDate previousMonth = today.minusMonths(1);
            safeDay = Math.min(closingDay, previousMonth.lengthOfMonth());

            endDate = LocalDate.of(
                    previousMonth.getYear(),
                    previousMonth.getMonth(),
                    safeDay
            );
        }

        return endDate;
    }
    public static LocalDate calculateStartDate(int closingDay, LocalDate today) {
        return calculateEndDate(closingDay, today).minusMonths(1);
    }

}
