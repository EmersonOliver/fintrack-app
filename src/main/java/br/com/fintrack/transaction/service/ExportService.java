package br.com.fintrack.transaction.service;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.repository.TransactionRepository;
import br.com.fintrack.user.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ExportService {

    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final UserService userService;


    public byte[] generateCsvByUser(UUID userId) {
        var user = userService.loadById(userId);

        if (user == null) throw new UsersException("Usuario nao encontrado");

        var cards = cardService.listAllByOwner(userId);
        StringBuilder sb = new StringBuilder();
        sb.append("cardId;description;lastDigits;transactionValue;date\n");
        for (CardEntity card : cards) {
            LocalDate transactionDateStart = LocalDate.now().withDayOfMonth(card.getClosingDate()).minusMonths(1L);
            LocalDate transactionDateEnd = LocalDate.now().withDayOfMonth(card.getClosingDate());

            List<TransactionEntity> transactions = transactionRepository.find("card.cardId =?1 and date > ?2 and date <=?3",
                    card.getCardId(), transactionDateStart, transactionDateEnd).list();

            for (TransactionEntity transaction : transactions) {
                sb.append(transaction.getCard().getCardId()).append(";")
                        .append(transaction.getDescription()).append(";")
                        .append(card.getLastDigits()).append(";")
                        .append(transaction.getInstallmentValue()).append(";")
                        .append(transaction.getDate()).append("\n");
            }
        }
        System.out.println(sb.toString());
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }


}
