package br.com.fintrack.transaction.resources;

import br.com.fintrack.core.security.AuthSecurityContext;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.service.ExportService;
import br.com.fintrack.transaction.service.TransactionService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Slf4j
@Path("/transactions")
@RequiredArgsConstructor
public class TransactionResource {

    private final TransactionService transactionService;
    private final ExportService exportService;
    private final AuthSecurityContext securityContext;

    @POST
    @Path("create")
    public Response create(TransactionRequest request) {
        log.info("Creating transaction payload= {}", request.toString());
        var entity = transactionService.create(request);
        return Response.ok(entity).build();
    }

    @POST
    @Path("update/{transactionId}")
    public Response updateTransaction(@PathParam("transactionId") UUID transactionId,

                                      TransactionRequest request) {
        log.info("Update transaction {}", request.toString());
        UUID userId = securityContext.getInstance().get().userId;
        var response = transactionService.updateTransaction(transactionId, userId, request);
        return Response.ok(response).encoding("UTF-8").build();
    }

    @POST
    @Path("create/bulk")
    public Response createLote(List<TransactionRequest> request) {
        log.info("Creating transaction payload= {}", request.toString());
        for (TransactionRequest transactionRequest : request) {
            transactionService.create(transactionRequest);
        }
        return Response.ok(request).build();
    }

    @GET
    @Path("load/{transactionId}")
    public Response loadTransactionById(@PathParam("transactionId") UUID transactionId) {
        UUID userId = securityContext.getInstance().get().userId;
        log.info("load transaction by ids");
        var response = transactionService.loadByTransactionId(transactionId, userId);
        return Response.ok(response).encoding("UTF-8").build();
    }

    @GET
    @Path("load/all")
    public Response loadAllTransactions(@HeaderParam("user-id") UUID userId) {
        log.info("loading all transactions by user");
        var response = transactionService.loadAllTransactions(userId);
        return Response.ok(response).encoding("UTF-8").build();
    }

    @GET
    @Path("export/csv")
    @Produces("text/csv")
    public Response exportByCardId() {
        UUID userId = securityContext.getInstance().get().userId;
        byte[] file = exportService.generateCsvByUser(userId);
        return Response.ok(file)
                .header("Content-Disposition",
                        "attachment; filename=transactions-" + userId + YearMonth.now() + ".csv")
                .build();
    }
}
