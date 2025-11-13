package br.com.fintrack.card.service.impl;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.repository.CardRepository;
import br.com.fintrack.card.resources.request.CardRequest;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.enums.CardType;
import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.user.service.UserService;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    @Override
    @Transactional
    public CardEntity saveCardAndReturnResult(CardRequest cardRequest, UUID userId) {
        var user = userService.loadById(userId);
        if(user == null) {
            throw new UsersException("Não foi possível gerar o registro pois o usuário é inválido");
        }

        CardEntity parentCard = null;
        if(cardRequest.cardType().equals(CardType.VIRTUAL)){
            if (cardRequest.parentCard() == null) {
                throw new UsersException("Cartões virtuais devem estar vinculados a um cartão físico");
            }

            parentCard = cardRepository.findById(UUID.fromString(cardRequest.parentCard()));
            if (parentCard == null) {
                throw new UsersException("Cartão físico não encontrado para vincular o virtual");
            }

            if (!parentCard.getOwner().getUserId().equals(userId)) {
                throw new UsersException("O cartão físico pertence a outro usuário");
            }
        }

        var card = CardEntity.builder()
                .cardType(cardRequest.cardType())
                .cardName(cardRequest.cardName())
                .lastDigits(cardRequest.lastDigits())
                .brandCard(cardRequest.brandCard())
                .active(cardRequest.active())
                .closingDate(cardRequest.closingDate())
                .dueDate(cardRequest.dueDate())
                .limitAvailable(cardRequest.limitAvailable())
                .limitTotal(cardRequest.limitTotal())
                .limitUsed(cardRequest.limitUsed())
                .owner(user)
                .parentCard(parentCard)
                .build();

        card.updateAvailableLimit();
        cardRepository.persistAndFlush(card);
        return card;
    }

    @Override
    public List<CardEntity> listAllByOwner(UUID ownerId) {
        return cardRepository.list("select c from CardEntity c where c.owner.userId=:userId order by active",
                Parameters.with("userId", ownerId));
    }

    @Override
    public Optional<CardEntity> loadCardById(String cardId) {
        return cardRepository.findByIdOptional(UUID.fromString(cardId));
    }

    @Override
    public List<CardEntity> listAll() {
        return cardRepository.listAll();
    }
}
