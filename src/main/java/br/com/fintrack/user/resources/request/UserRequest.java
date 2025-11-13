package br.com.fintrack.user.resources.request;

public record UserRequest(String name,
                          String email,
                          String password) {
}
