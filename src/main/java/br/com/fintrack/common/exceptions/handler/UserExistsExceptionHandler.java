package br.com.fintrack.common.exceptions.handler;

import br.com.fintrack.common.exceptions.UsersException;
import br.com.fintrack.common.messages.ResponseMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider
public class UserExistsExceptionHandler implements ExceptionMapper<UsersException> {
    @Override
    public Response toResponse(UsersException exception) {
        var message = new ResponseMessage("X01", exception.getMessage(), LocalDateTime.now());
        return Response.status(Response.Status.BAD_REQUEST).entity(message).encoding("UTF-8").build();
    }
}
