package br.com.fintrack.card.domain;

import br.com.fintrack.common.enums.BrandCard;
import br.com.fintrack.common.enums.CardType;
import br.com.fintrack.invoice.domain.InvoiceEntity;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.user.domain.UserEntity;
import br.com.fintrack.wallet.domain.WalletEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card", schema = "fintrack")
public class CardEntity implements Serializable {

    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cardId;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "last_digits")
    private String lastDigits;

    @Column(name = "brand_card")
    private BrandCard brandCard;

    @Column(name = "due_date")
    private Integer dueDate;

    @Column(name = "closing_date")
    private Integer closingDate;

    @Column(name = "limit_total")
    private BigDecimal limitTotal;

    @Column(name = "limit_used")
    private BigDecimal limitUsed;

    @Column(name = "limit_available")
    private BigDecimal limitAvailable;

    @Column(name = "active")
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private CardType cardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_card_owner_user"))
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", foreignKey = @ForeignKey(name = "fk_wallet_card"))
    private WalletEntity wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_card_id", referencedColumnName = "card_id")
    private CardEntity parentCard;

    @OneToMany(mappedBy = "parentCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardEntity> virtualCards;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionEntity> transactions;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceEntity> invoices;

    public void updateAvailableLimit() {
        if (limitTotal != null && limitUsed != null)
            this.limitAvailable = this.limitTotal.subtract(this.limitUsed);
    }

}
