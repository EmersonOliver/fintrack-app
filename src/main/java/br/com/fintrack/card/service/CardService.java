package br.com.fintrack.card.service;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.resources.request.CardRequest;
import br.com.fintrack.user.domain.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardService {

    CardEntity saveCardAndReturnResult(CardRequest cardRequest, UUID userId);
    List<CardEntity> listAllByOwner(UUID ownerId);
    Optional<CardEntity> loadCardById(String cardId);
    List<CardEntity> listAll();
}
