package br.com.fintrack.common.exceptions.handler;

import br.com.fintrack.common.messages.ResponseMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        var message = new ResponseMessage("X09", exception.getMessage(), LocalDateTime.now());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(message).encoding("UTF-8").build();
    }
}
