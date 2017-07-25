package app.service;

import app.data.Token;

import java.util.Set;
import java.util.UUID;

public class DummyTokenService implements TokenService {
    @Override
    public Token createToken(final String userName, final Set<String> roles,
                             final int version) {
        Token authToken = new Token();
        authToken.setAuthToken(UUID.randomUUID().toString());
        return authToken;
    }

    @Override
    public Token createToken(String jwtString) {
        return null;
    }
}
