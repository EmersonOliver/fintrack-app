package br.com.fintrack.user.resources;

import br.com.fintrack.common.messages.ResponseMessage;
import br.com.fintrack.user.resources.request.UserRequest;
import br.com.fintrack.user.resources.response.UserResponse;
import br.com.fintrack.user.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {


    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Path("create")
    public Response createNewUser(UserRequest userRequest) {
        userService.createNewUser(userRequest);
        var message = new ResponseMessage("01", "Created successfully", LocalDateTime.now());
        return Response.ok(message)
                .build();
    }


    @POST
    @Path("login")
    public Response userLogin(UserRequest userRequest) {
        Map<String, String> mapResponse = new HashMap<>();
        mapResponse.put("token", userService.getTokenUser(userRequest.email(), userRequest.password()));
        return Response.ok(mapResponse).build();
    }

    @GET
    @Path("load/{userId}")
    public Response loadUserById(@PathParam("userId") String userId) {
        var user = userService.loadById(UUID.fromString(userId));
        return Response.ok(UserResponse.fromEntity(user)).build();
    }


}
