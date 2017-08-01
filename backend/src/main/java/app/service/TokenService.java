package app.service;

import app.data.Token;
import app.data.User;
import app.exception.InvalidTokenException;

public interface TokenService {
    Token forUser(User user);
    Token forJwtString(String jwtString);
}
