package br.com.fintrack.invoice.resources.response;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.resources.response.CardResponse;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.resources.response.TransactionResponse;
import br.com.fintrack.user.resources.response.UserResponse;
import br.com.fintrack.wallet.domain.WalletEntity;
import br.com.fintrack.wallet.resources.response.WalletResponse;

import java.math.BigDecimal;

public record CardInvoicePaymentResponse(
        WalletResponse wallet,
        CardResponse card,
        InvoiceResponse invoice
) {

    public static CardInvoicePaymentResponse fromEntity(WalletEntity walletEntity, InvoiceEntity invoiceEntity) {

        return new CardInvoicePaymentResponse(WalletResponse.fromEntity(walletEntity),
                CardResponse.fromEntity(invoiceEntity.getCard()),
                InvoiceResponse.fromEntity(invoiceEntity));
    }


}
