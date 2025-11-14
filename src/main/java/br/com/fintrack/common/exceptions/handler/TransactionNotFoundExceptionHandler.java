package br.com.fintrack.common.exceptions.handler;

import br.com.fintrack.common.exceptions.TransactionNotFoundException;
import br.com.fintrack.common.messages.ResponseMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Provider
public class TransactionNotFoundExceptionHandler implements ExceptionMapper<TransactionNotFoundException> {
    @Override
    public Response toResponse(TransactionNotFoundException exception) {
        var message = new ResponseMessage("X45", exception.getMessage(), LocalDateTime.now());
        return Response.status(Response.Status.BAD_REQUEST).entity(message).encoding("UTF-8").build();
    }
}
