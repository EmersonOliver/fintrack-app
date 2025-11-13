package br.com.fintrack.transaction.resources;

import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.service.TransactionService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/transactions")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    private final TransactionService transactionService;

    @POST
    @Path("create")
    public Response create(TransactionRequest request) {
        var entity = transactionService.create(request);
        return Response.ok(entity).build();
    }
}
