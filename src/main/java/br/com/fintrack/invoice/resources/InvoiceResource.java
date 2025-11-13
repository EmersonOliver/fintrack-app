package br.com.fintrack.invoice.resources;


import br.com.fintrack.invoice.repository.InvoiceRepository;
import br.com.fintrack.invoice.resources.response.InvoiceResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path("invoices")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InvoiceResource {

    private final InvoiceRepository repository;


    @GET
    @Path("byCard/{cardId}")
    public Response loadInvoiceByCard(@PathParam("cardId") String cardId) {
        List<InvoiceResponse> findAll = repository.findAll().stream().map(InvoiceResponse::fromEntity)
                .toList();
        return Response.ok(findAll).build();
    }


}
