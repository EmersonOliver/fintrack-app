package br.com.fintrack.wallet.resources;

import br.com.fintrack.core.security.AuthSecurityContext;
import br.com.fintrack.wallet.resources.request.WalletRequest;
import br.com.fintrack.wallet.resources.response.WalletResponse;
import br.com.fintrack.wallet.service.WalletService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Path("wallets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class WalletResource {

    private final WalletService walletService;
    private final AuthSecurityContext authSecurityContext;

    @POST
    @Path("create")
    public Response create(WalletRequest request) {
        UUID ownerId = authSecurityContext.getInstance().get().userId;
        WalletResponse wallet = walletService.create(request, ownerId);
        return Response.status(Response.Status.CREATED).entity(wallet).build();
    }

    @GET
    @Path("/owner")
    public Response listByOwner(@QueryParam("page") @DefaultValue("0") Integer page,
                                @QueryParam("size") @DefaultValue("10") Integer size) {
        UUID ownerId = authSecurityContext.getInstance().get().userId;
        var wallets = walletService.listByOwner(ownerId, page, size);
        return Response.ok(wallets).build();
    }

    @GET
    @Path("/{walletId}")
    public Response findById(@PathParam("walletId") UUID walletId) {
        WalletResponse wallet = walletService.findById(walletId);
        return Response.ok(wallet).build();
    }

    @PUT
    @Path("/{walletId}")
    public Response update(@PathParam("walletId") UUID walletId, WalletRequest request) {
        WalletResponse wallet = walletService.update(walletId, request);
        return Response.ok(wallet).build();
    }

    @DELETE
    @Path("/{walletId}")
    public Response delete(@PathParam("walletId") UUID walletId) {
        walletService.delete(walletId);
        return Response.noContent().build();
    }

}
