package br.com.fintrack.card.resources;

import br.com.fintrack.card.domain.CardEntity;
import br.com.fintrack.card.resources.request.CardRequest;
import br.com.fintrack.card.resources.response.CardResponse;
import br.com.fintrack.card.service.CardService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("card")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CardResource {

    private final CardService cardService;

    public CardResource(CardService cardService) {
        this.cardService = cardService;
    }

    @POST
    @Path("create")
    public Response postCard(CardRequest request, @HeaderParam("user-id") String userId) {
        CardEntity card = cardService.saveCardAndReturnResult(request, UUID.fromString(userId));
        return Response.ok(CardResponse.fromEntity(card)).encoding("utf-8").build();
    }

    @GET
    @Path("load")
    public Response getCard(@QueryParam("cardId") String cardId) {
        var card = cardService.loadCardById(cardId);
        if (card.isPresent()) {
            return Response.ok(CardResponse.fromEntity(card.get())).encoding("UTF-8").build();
        }
        return Response.noContent().build();
    }
}
