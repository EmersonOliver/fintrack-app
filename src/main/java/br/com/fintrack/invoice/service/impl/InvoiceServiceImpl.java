package br.com.fintrack.invoice.service.impl;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.common.enums.StatusTransaction;
import br.com.fintrack.common.exceptions.InvoiceException;
import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.invoice.repository.InvoiceRepository;
import br.com.fintrack.invoice.resources.request.InvoiceRequest;
import br.com.fintrack.invoice.resources.response.InvoiceResponse;
import br.com.fintrack.invoice.resources.response.SummaryInvoice;
import br.com.fintrack.invoice.service.InvoiceService;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.repository.TransactionRepository;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
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
    public InvoiceResponse getInvoiceById(Long invoiceId) {
        return invoiceRepository.findByIdOptional(invoiceId)
                .map(InvoiceResponse::fromEntity).orElseThrow(() ->
                        new InvoiceException("Invoice Not Found!"));
    }

    @Override
    public InvoiceEntity getInvoiceEntityById(Long invoiceId) {
        return invoiceRepository.findByIdOptional(invoiceId)
                .orElseThrow(() -> new InvoiceException("Invoice not found by id"));
    }

    @Override
    public InvoiceEntity getOrCreateInvoice(CardEntity card, YearMonth reference) {
        return invoiceRepository
                .findOpenByCardAndReference(card.getCardId(), reference.getYear(), reference.getMonthValue())
                .orElseGet(() -> createInvoice(card, reference));
    }

    @Override
    public void updateInvoiceEntity(InvoiceEntity invoiceEntity) {
        this.invoiceRepository.persist(invoiceEntity);
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

    @Override
    @Transactional
    public void recalculateInvoice(InvoiceEntity invoice) {
        BigDecimal total = invoice.getTransactions().stream()
                .map(TransactionEntity::getInstallmentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paid = invoice.getTransactions().stream()
                .map(tr -> tr.getPaidAmount() == null
                        ? BigDecimal.ZERO
                        : tr.getPaidAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setTotalAmount(total);
        invoice.setPaidAmount(paid);

        BigDecimal remaining = total.subtract(paid);
        invoice.setRemainingAmount(remaining);

        if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        } else {
            invoice.setStatus(InvoiceStatus.OPEN);
        }

        invoiceRepository.persist(invoice);
    }

    @Override
    @Transactional
    public void applyInvoicePayment(Long invoiceId, TransactionRequest request) {
        InvoiceEntity currentInvoice = getInvoiceEntityById(invoiceId);

        BigDecimal remainingPayment = request.amount();

        remainingPayment = applyPaymentToInvoice(currentInvoice, remainingPayment);

        if (remainingPayment.compareTo(BigDecimal.ZERO) > 0) {
            applyPaymentToFutureInvoices(
                    currentInvoice.getCard(),
                    YearMonth.of(currentInvoice.getReferenceYear(),
                            currentInvoice.getReferenceMonth()),
                    remainingPayment
            );
        }
    }
    private BigDecimal applyPaymentToInvoice(InvoiceEntity invoice, BigDecimal payment) {

        BigDecimal invoiceRemaining =
                invoice.getTotalAmount().subtract(invoice.getPaidAmount());

        BigDecimal applied =
                payment.min(invoiceRemaining);

        invoice.setPaidAmount(invoice.getPaidAmount().add(applied));

        if (invoice.getPaidAmount().compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        updateInvoiceEntity(invoice);

        // distribui impacto nas parcelas
        distributePaymentOnTransactions(invoice, applied);

        return payment.subtract(applied);
    }
    private void distributePaymentOnTransactions(
            InvoiceEntity invoice,
            BigDecimal payment
    ) {

        List<TransactionEntity> installments =
                transactionService.findByInvoiceOrdered(invoice);

        BigDecimal remaining = payment;

        for (TransactionEntity tx : installments) {

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal txRemaining =
                    tx.getInstallmentValue()
                            .subtract(tx.getPaidAmount());

            BigDecimal applied = remaining.min(txRemaining);

            tx.setPaidAmount(tx.getPaidAmount().add(applied));

            if (tx.getPaidAmount().compareTo(tx.getInstallmentValue()) >= 0) {
                tx.setStatus(StatusTransaction.PAID);
            } else {
                tx.setStatus(StatusTransaction.PARTIALLY_PAID);
            }

            transactionService.persist(tx);
            remaining = remaining.subtract(applied);
        }
    }

    private void applyPaymentToFutureInvoices(
            CardEntity card,
            YearMonth currentPeriod,
            BigDecimal remainingPayment
    ) {

        List<InvoiceEntity> futureInvoices =
                invoiceRepository.findOpenInvoicesAfter(card, currentPeriod);

        BigDecimal remaining = remainingPayment;

        for (InvoiceEntity invoice : futureInvoices) {

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            remaining = applyPaymentToInvoice(invoice, remaining);
        }
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
                reference.getYear(),
                reference.getMonth(),
                Math.min(card.getDueDate(), reference.lengthOfMonth())
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
