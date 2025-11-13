package br.com.fintrack.invoice.service.impl;

import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.invoice.repository.InvoiceRepository;
import br.com.fintrack.invoice.resources.request.InvoiceRequest;
import br.com.fintrack.invoice.resources.response.InvoiceResponse;
import br.com.fintrack.invoice.service.InvoiceService;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CardService cardService;

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
}
