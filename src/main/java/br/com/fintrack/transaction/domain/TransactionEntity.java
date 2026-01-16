package br.com.fintrack.transaction.domain;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.common.enums.PaymentMethod;
import br.com.fintrack.common.enums.StatusTransaction;
import br.com.fintrack.common.enums.TransactionType;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.wallet.domain.WalletEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction", schema = "fintrack")
public class TransactionEntity implements Serializable {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID transactionId;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Column(name = "installment_value")
    private BigDecimal installmentValue;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "type")
    private TransactionType type;

    @Column(name = "method")
    private PaymentMethod method;

    @Column(name = "installment")
    private Boolean installment;

    @Column(name = "generated")
    private Boolean generated;

    @Column(name = "root_installment")
    private Boolean rootInstallment;

    @Column(name = "installment_number")
    private Integer installmentNumber;

    @Column(name = "installment_total")
    private Integer installmentTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private StatusTransaction status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", referencedColumnName = "card_id",
            foreignKey = @ForeignKey(name = "fk_transaction_card"))
    private CardEntity card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "wallet_id",
            foreignKey = @ForeignKey(name = "fk_transaction_wallet"))
    private WalletEntity wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_transaction", referencedColumnName = "user_id",
            foreignKey = @ForeignKey(name = "fk_transaction_user"))
    private UserEntity userTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_transaction_id", referencedColumnName = "invoice_id",
            foreignKey = @ForeignKey(name = "fk_invoice_transaction"))
    private InvoiceEntity invoice;

    @PrePersist
    @PreUpdate
    private void validateState() {

        if (type == TransactionType.INCOME) {
            if (card != null || invoice != null || Boolean.TRUE.equals(installment)) {
                throw new IllegalStateException("INCOME não pode ter cartão, fatura ou parcelamento");
            }
        }

        if (method == PaymentMethod.DEBIT && Boolean.TRUE.equals(installment)) {
            throw new IllegalStateException("Débito não pode ser parcelado");
        }

        if (method == PaymentMethod.CREDIT && installmentTotal != null && installmentTotal > 1 && invoice == null) {
            throw new IllegalStateException("Crédito parcelado deve possuir fatura");
        }
    }
}
