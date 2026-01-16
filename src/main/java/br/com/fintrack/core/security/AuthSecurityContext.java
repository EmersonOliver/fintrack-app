package br.com.fintrack.core.security;

import jakarta.enterprise.context.RequestScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@RequestScoped
public class AuthSecurityContext {

    private static ThreadLocal<SecurityData> instance = new ThreadLocal<>();

    public void initialize() {
        instance.set(new SecurityData());
    }

    public ThreadLocal<SecurityData> getInstance() {
        return instance;
    }

    public class SecurityData {

        public String userEmail;
        public String ipAdress;
        public String email;
        public UUID userId;
        public JsonWebToken jsonWebToken;

    }
}
