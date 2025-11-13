package br.com.fintrack.wallet.domain;

import br.com.fintrack.common.enums.WalletType;
import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.user.domain.UserEntity;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallets", schema = "fintrack")
public class WalletEntity implements Serializable {

    @Id
    @Column(name = "wallet_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID walletId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "wallet_name", nullable = false)
    private String walletName;

    @Column(name = "wallet_type")
    private WalletType walletType;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id",
            updatable = false, insertable = false,
            foreignKey = @ForeignKey(name = "fk_user_id_wallet_id"))
    private UserEntity owner;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionEntity> transactions;

}
