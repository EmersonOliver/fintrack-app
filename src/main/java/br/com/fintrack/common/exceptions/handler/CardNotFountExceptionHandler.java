package br.com.fintrack.common.exceptions.handler;

import br.com.fintrack.common.exceptions.CardNotFoundException;
import br.com.fintrack.common.messages.ResponseMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Provider
public class CardNotFountExceptionHandler implements ExceptionMapper<CardNotFoundException> {

    @Override
    public Response toResponse(CardNotFoundException exception) {
        var message = new ResponseMessage("X35", exception.getMessage(), LocalDateTime.now());
        log.info("{}", message);
        return Response.status(Response.Status.BAD_REQUEST).entity(message).encoding("UTF-8").build();
    }
}
