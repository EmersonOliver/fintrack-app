package br.com.fintrack.common.exceptions.handler;

import br.com.fintrack.common.messages.ResponseMessage;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Provider
public class WebApplicationExceptionHandler implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException e) {
        var message = new ResponseMessage("X04", e.getMessage(), LocalDateTime.now());
        return Response.status(e.getResponse().getStatus())
                .entity(message).encoding("UTF-8").build();
    }
}
