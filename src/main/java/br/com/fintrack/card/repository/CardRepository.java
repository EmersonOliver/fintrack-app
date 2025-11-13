package br.com.fintrack.card.repository;

import br.com.fintrack.card.domain.CardEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CardRepository implements PanacheRepositoryBase<CardEntity, UUID> {

}
