package br.com.fintrack.user.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.nio.file.Files;

@Path("/uploads")
public class UploadResource {

    @GET
    @Path("/profiles/{file}")
    @Produces({"image/jpeg", "image/png"})
    public Response getProfileImage(@PathParam("file") String file)
            throws IOException {
        java.nio.file.Path path = java.nio.file.Path.of("uploads/profiles").resolve(file);
        return Response.ok(Files.readAllBytes(path)).build();
    }
}
