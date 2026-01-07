package br.com.fintrack.invoice.domain;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.common.enums.InvoiceStatus;
import br.com.fintrack.transaction.domain.TransactionEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice", schema = "fintrack")
@SequenceGenerator(name = "sq_invoice_id", sequenceName = "seq_invoice_id", allocationSize = 1, schema = "fintrack")
public class InvoiceEntity implements Serializable {

    @Id
    @Column(name = "invoice_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_invoice_id")
    private Long invoiceId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private CardEntity card;

    @Column(name = "reference_year", nullable = false)
    private Integer referenceYear;

    @Column(name = "reference_month", nullable = false)
    private Integer referenceMonth;

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "status")
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice")
    private List<TransactionEntity> transactions;

    @Transient
    public YearMonth getReference() {
        return YearMonth.of(referenceYear, referenceMonth);
    }
}
