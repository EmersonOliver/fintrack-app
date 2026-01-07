package br.com.fintrack.invoice.service.impl;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.invoice.repository.InvoiceRepository;
import br.com.fintrack.invoice.resources.request.InvoiceRequest;
import br.com.fintrack.invoice.resources.response.InvoiceResponse;
import br.com.fintrack.invoice.resources.response.SummaryInvoice;
import br.com.fintrack.invoice.service.InvoiceService;
import br.com.fintrack.transaction.service.TransactionService;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CardService cardService;
    private final TransactionService transactionService;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveInvoice(InvoiceRequest request, UUID cardId) {

        log.info("Starting persisting Invoice....");
        var optionalCardEntity = cardService.loadCardById(cardId.toString());
        if (optionalCardEntity.isEmpty()) {
            log.warn("Necessario ter um cartao valido, as informacoes inseridas nao correspondem com a solicitada.");
            throw new UsersException("Necessario ter um cartao valido para gerar a sua fatura!");
        }
        var entityInvoice = request.fromEntity();
        entityInvoice.setCard(optionalCardEntity.get());

        invoiceRepository.persist(entityInvoice);
        log.info("Invoice Saved Successfully");
    }

    @Override
    public InvoiceEntity getOrCreateInvoice(CardEntity card, YearMonth reference) {
        return invoiceRepository
                .findOpenByCardAndReference(card.getCardId(), reference.getYear(), reference.getMonthValue())
                .orElseGet(() -> createInvoice(card, reference));
    }

    @Override
    public List<InvoiceResponse> findAllInvoiceByCardIdWithResponse(final String cardId) {
        var invoicesEntityList = invoiceRepository.find("where card.cardId =:cardId ",
                Parameters.with("cardId", cardId)).list();
        if (invoicesEntityList.isEmpty()) {
            return List.of();
        }
        return invoicesEntityList.stream().map(InvoiceResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public InvoiceResponse updatedInvoice(Long invoiceId, InvoiceRequest request) {
        var loadedInvoiceEntity = invoiceRepository.findByIdOptional(invoiceId);
        if (loadedInvoiceEntity.isPresent()) {
            var setInvoiceUpdate = loadedInvoiceEntity.get();
            setInvoiceUpdate.setTotalAmount(request.totalAmount());
            setInvoiceUpdate.setStatus(request.status());
            setInvoiceUpdate.setPeriodEnd(request.periodEnd());
            setInvoiceUpdate.setPeriodStart(request.periodStart());
            invoiceRepository.persistAndFlush(setInvoiceUpdate);
            return InvoiceResponse.fromEntity(setInvoiceUpdate);
        }
        throw new UsersException("Ocorreu uma falha para carregar a fatura");
    }

    @Override
    public List<SummaryInvoice> getResumeInvoice(String referenceDate, UUID userId) {
        var listCards = cardService.listAllByOwner(userId);
        List<SummaryInvoice> summary = new ArrayList<>();

        for (CardEntity card : listCards) {
            LocalDate startDate = referenceDateParse(referenceDate, card.getClosingDate());
            var listTransactions = transactionService.loadTransactionsByCard(card.getOwner().getUserId(),
                    card.getCardId(), startDate, startDate.plusMonths(1L));
            summary.add(SummaryInvoice.summaryInvoiceResponse(card, listTransactions, startDate));
        }
        return summary;
    }

    private LocalDate referenceDateParse(String referenceDate, Integer closingDate) {
        return LocalDate.parse(referenceDate + "-" + closingDate);
    }

    private InvoiceEntity createInvoice(CardEntity card, YearMonth reference) {

        LocalDate periodStart = LocalDate.of(
                reference.getYear(),
                reference.getMonth(),
                Math.min(card.getClosingDate(), reference.lengthOfMonth())
        );

        LocalDate periodEnd = LocalDate.of(
                reference.plusMonths(1).getYear(),
                reference.plusMonths(1).getMonth(),
                Math.min(card.getDueDate(), reference.plusMonths(1).lengthOfMonth())
        );

        InvoiceStatus status = resolveStatus(reference);

        InvoiceEntity invoice = InvoiceEntity.builder()
                .card(card)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .createdAt(LocalDateTime.now())
                .status(status)
                .totalAmount(BigDecimal.ZERO)
                .referenceYear(reference.getYear())
                .referenceMonth(reference.getMonthValue())
                .build();

        invoiceRepository.persist(invoice);
        return invoice;
    }

    private InvoiceStatus resolveStatus(YearMonth reference) {
        YearMonth now = YearMonth.now();

        if (reference.isAfter(now)) {
            return InvoiceStatus.FUTURE;
        }
        if (reference.equals(now)) {
            return InvoiceStatus.OPEN;
        }
        return InvoiceStatus.CLOSED;
    }


}
