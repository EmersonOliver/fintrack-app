package br.com.fintrack.card.resources;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.resources.request.CardRequest;
import br.com.fintrack.card.resources.response.CardResponse;
import br.com.fintrack.card.service.CardService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Path("card")
@RequiredArgsConstructor
public class CardResource {

    private final CardService cardService;

    @POST
    @Path("create")
    public Response postCard(CardRequest request, @HeaderParam("user-id") String userId) {
        log.info("Creating card type={} ", request.cardType().name());
        CardEntity card = cardService.saveCardAndReturnResult(request, UUID.fromString(userId));
        return Response.ok(CardResponse.fromEntity(card)).encoding("utf-8").build();
    }

    @PUT
    @Path("update")
    public Response updateCard(@HeaderParam("user-id") UUID userId,
                               @QueryParam("cardId") UUID cardID,
                               CardRequest request) {
        log.info("Updating card transaction {}", request.toString());
        var response = cardService.updateCard(userId, cardID, request);
        return Response.ok(response).encoding(
                "UTF-8"
        ).build();
    }

    @DELETE
    @Path("delete")
    public Response deleteCard(@QueryParam("cardId") UUID cardId, @HeaderParam("user-id") UUID userId) {
        log.info("Removing card type");
        cardService.deleteCard(cardId, userId);
        return Response.accepted().build();
    }

    @GET
    @Path("load")
    public Response getCard(@QueryParam("cardId") String cardId) {
        log.info("Load card By Id");
        var card = cardService.loadCardById(cardId);
        if (card.isPresent()) {
            return Response.ok(CardResponse.fromEntity(card.get())).encoding("UTF-8").build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("load/owner")
    public Response loadCardByUser(@HeaderParam("user-id") UUID userId) {
        var result = cardService.listAllByOwner(userId);
        List<CardResponse> response = new ArrayList<>();
        if (!result.isEmpty()) {
            response.addAll(result.stream().map(CardResponse::fromEntity).toList());
        }
        return Response.ok(response).build();
    }
}
