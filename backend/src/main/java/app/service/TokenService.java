package app.service;

import app.data.Token;

import java.util.Set;

public interface TokenService {
    Token createToken(String userName, Set<String> roles, int version);

    Token createToken(String jwtString);
}
