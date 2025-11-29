package br.com.fintrack.user.service;

public interface AuthService {
    String getTokenUser(String email, String password);
}
