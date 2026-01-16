package br.com.fintrack.invoice.resources.controller;

import br.com.fintrack.card.service.CardService;
import br.com.fintrack.invoice.resources.response.CardInvoicePaymentResponse;
import br.com.fintrack.invoice.service.InvoiceService;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.service.TransactionService;
import br.com.fintrack.wallet.service.WalletService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PaymentInvoiceController {

    private final InvoiceService invoiceService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final CardService cardService;


    public void cardInvoicePaymentResponseMapper(Long invoiceId, TransactionRequest request) {
        invoiceService.applyInvoicePayment(invoiceId, request);
    }
}
