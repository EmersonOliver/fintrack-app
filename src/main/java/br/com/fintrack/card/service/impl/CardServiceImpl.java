package br.com.fintrack.card.service.impl;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.repository.CardRepository;
import br.com.fintrack.card.resources.request.CardRequest;
import br.com.fintrack.card.resources.response.CardResponse;
import br.com.fintrack.card.service.CardService;
import br.com.fintrack.common.exceptions.CardNotFoundException;
import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.user.service.UserService;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.fintrack.common.enums.CardType.VIRTUAL;

@ApplicationScoped
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    @Override
    @Transactional
    public CardEntity saveCardAndReturnResult(CardRequest cardRequest, UUID userId) {
        var user = userService.loadById(userId);
        if (user == null) {
            throw new UsersException("Não foi possível gerar o registro pois o usuário é inválido");
        }

        CardEntity parentCard = null;
        if (cardRequest.cardType().equals(VIRTUAL)) {
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
                .limitAvailable(cardRequest.limitAvailable() != null ? cardRequest.limitAvailable() : cardRequest.limitTotal())
                .limitTotal(cardRequest.limitTotal())
                .limitUsed(cardRequest.limitUsed() != null ? cardRequest.limitUsed() : BigDecimal.ZERO)
                .owner(user)
                .parentCard(parentCard)
                .build();

        card.updateAvailableLimit();
        cardRepository.persistAndFlush(card);
        return card;
    }

    @Override
    @Transactional
    public CardResponse updateCard(UUID userId, UUID cardId, CardRequest request) {
        var user = userService.loadById(userId);
        if (user == null) {
            throw new UsersException("Usuário inválido");
        }

        var principalCard = cardRepository.findByIdOptional(cardId)
                .orElseThrow(() -> new CardNotFoundException("Cartão não encontrado"));

        // Segurança: garantir que o usuário é dono do cartão
        if (!principalCard.getOwner().getUserId().equals(userId)) {
            throw new UsersException("Você não pode atualizar um cartão que não pertence a você");
        }

        // Nova regra: resolver mudanças de tipo (físico <-> virtual)
        CardEntity newParent = resolveTypeTransition(principalCard, request, userId);

        // Atualizações
        principalCard.setCardType(request.cardType());
        principalCard.setCardName(request.cardName());
        principalCard.setLastDigits(request.lastDigits());
        principalCard.setBrandCard(request.brandCard());
        principalCard.setActive(request.active());
        principalCard.setClosingDate(request.closingDate());
        principalCard.setDueDate(request.dueDate());
        principalCard.setLimitAvailable(request.limitAvailable());
        principalCard.setLimitTotal(request.limitTotal());
        principalCard.setLimitUsed(request.limitUsed());
        principalCard.setParentCard(newParent);

        principalCard.updateAvailableLimit();

        cardRepository.persistAndFlush(principalCard);

        return CardResponse.fromEntity(principalCard);
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

    @Override
    @Transactional
    public void deleteCard(UUID cardId, UUID userId) {
        var user = userService.loadById(userId);
        if (user == null) {
            throw new UsersException("Usuário não localizado");
        }
        var cardEntity = cardRepository.findById(cardId);
        if (cardEntity == null) {
            throw new CardNotFoundException("Cartão não encontrado");
        }
        if (!cardEntity.getOwner().getUserId().equals(user.getUserId())) {
            throw new CardNotFoundException("Cartão não pertence ao seu usuário");
        }
        if(!cardEntity.getVirtualCards().isEmpty()){
            cardEntity.getVirtualCards().forEach(card-> {
                card.setActive(false);
            });
        }
        cardEntity.setActive(false);
        cardRepository.persistAndFlush(cardEntity);
    }

    @Override
    @Transactional
    public void updateLimit(CardEntity card) {
        cardRepository.persistAndFlush(card);
    }

    private CardEntity resolveTypeTransition(CardEntity existingCard, CardRequest request, UUID userId) {

        boolean wasVirtual = existingCard.getCardType() == VIRTUAL;
        boolean willBeVirtual = request.cardType() == VIRTUAL;

        if (!wasVirtual && !willBeVirtual) {
            return null;
        }
        if (wasVirtual && willBeVirtual) {
            return validateAndLoadParent(request, userId);
        }
        if (wasVirtual && !willBeVirtual) {
            throw new UsersException("Não é permitido converter cartão virtual em cartão físico");
        }
        if (!wasVirtual && willBeVirtual) {
            return validateAndLoadParent(request, userId);
        }
        throw new UsersException("Transição de tipo inválida"); // fallback
    }

    private CardEntity validateAndLoadParent(CardRequest request, UUID userId) {

        if (request.parentCard() == null) {
            throw new CardNotFoundException("Cartões virtuais devem estar vinculados a um cartão físico");
        }
        UUID parentId;
        try {
            parentId = UUID.fromString(request.parentCard());
        } catch (Exception e) {
            throw new CardNotFoundException("parentCard inválido");
        }
        CardEntity parentCard = cardRepository.findById(parentId);
        if (parentCard == null) {
            throw new CardNotFoundException("Cartão físico não encontrado");
        }
        if (!parentCard.getOwner().getUserId().equals(userId)) {
            throw new UsersException("O cartão físico pertence a outro usuário");
        }
        if (parentCard.getCardType() == VIRTUAL) {
            throw new UsersException("Não é permitido vincular um cartão virtual como cartão pai");
        }
        return parentCard;
    }
}
