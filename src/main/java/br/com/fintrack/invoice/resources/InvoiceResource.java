package br.com.fintrack.invoice.resources;


import br.com.fintrack.core.security.AuthSecurityContext;
import br.com.fintrack.invoice.resources.controller.PaymentInvoiceController;
import br.com.fintrack.invoice.resources.request.InvoicePaymentRequest;
import br.com.fintrack.invoice.resources.request.InvoiceRequest;
import br.com.fintrack.invoice.resources.response.InvoiceResponse;
import br.com.fintrack.invoice.resources.response.SummaryInvoice;
import br.com.fintrack.invoice.service.InvoiceService;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@Path("invoices")
@RequiredArgsConstructor
public class InvoiceResource {

    private final InvoiceService service;
    private final AuthSecurityContext securityContext;
    private final PaymentInvoiceController paymentInvoiceController;

    @POST
    @Path("create")
    public Response createInvoiceByCardManualy(@QueryParam("cardId") UUID cardId,
                                               InvoiceRequest request) {
        log.info("Requesting createInvoiceByCardManualy cardId={} payload= {}", cardId, request.toString());
        service.saveInvoice(request, cardId);
        log.info("Saved successfully");
        return Response.ok().build();
    }


    @GET
    @Path("find/card/{cardId}")
    public Response loadInvoiceByCard(@PathParam("cardId") @Nonnull String cardId) {
        List<InvoiceResponse> findAll = service.findAllInvoiceByCardIdWithResponse(cardId);
        return Response.ok(findAll).build();
    }

    @PUT
    @Path("update")
    public Response updateInvoice(@QueryParam("invoiceId") Long invoiceId, InvoiceRequest request) {
        log.info("Updating invoice invoiceId={}, payload={}", invoiceId, request.toString());
        var invoiceResponse = service.updatedInvoice(invoiceId, request);
        log.info("Fatura atualizada periodStart={}, periodEnd={}, status={}, amount={}", invoiceResponse.periodStart(),
                invoiceResponse.periodEnd(), invoiceResponse.status(),
                invoiceResponse.totalAmount());
        return Response.ok(invoiceResponse).encoding("UTF-8").build();
    }

    @GET
    @Path("resume/{referenceDate}")
    public Response resumeMonth(@PathParam("referenceDate")
                                @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])$", message = "Formato inválido. Use yyyy-MM")
                                String referenceDate) {
        List<SummaryInvoice> result = this.service.getResumeInvoice(referenceDate, securityContext.getInstance().get().userId);
        return Response.ok(result).build();

    }

    @GET
    @Path("{invoiceId}")
    public Response getInvoiceById(@PathParam("invoiceId") Long invoiceId) {
        var response = service.getInvoiceById(invoiceId);
        return Response.ok(response).build();
    }

    @PUT
    @Path("paid/{invoiceId}")
    public Response paidInvoice(@PathParam("invoiceId") Long invoiceId, TransactionRequest transactionRequest) {
        paymentInvoiceController.cardInvoicePaymentResponseMapper(invoiceId, transactionRequest);
        return Response.ok().build();
    }


}
