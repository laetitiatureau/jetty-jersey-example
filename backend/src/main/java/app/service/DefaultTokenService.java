package app.service;

import app.data.Token;
import app.data.User;
import app.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;
import java.time.Instant;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Date;

public class DefaultTokenService implements TokenService {
    private static final SignatureAlgorithm SIGNING_ALGORITHM = SignatureAlgorithm.HS512;
    private static final String CLAIM_USERNAME = "username";

    private final TemporalAmount expiryTime;
    private final Key signingKey;

    public DefaultTokenService(final Key signingKey, TemporalAmount expiryTime) {
        this.signingKey = signingKey;
        this.expiryTime = expiryTime;
    }

    public DefaultTokenService() {
        this(MacProvider.generateKey(SignatureAlgorithm.HS512), Period.ofDays(1));
    }

    @Override
    public Token forUser(final User user) {
        Token authToken = new Token();
        authToken.setToken(createTokenString(user.getName()));
        authToken.setUsername(user.getName());
        return authToken;
    }

    @Override
    public Token forJwtString(String jwtString) throws InvalidTokenException {
        Claims claims = verifyTokenString(jwtString);

        String username = claims.get(CLAIM_USERNAME, String.class);

        if (username == null) {
            throw new InvalidTokenException();
        }

        Token token = new Token();
        token.setToken(jwtString);
        token.setUsername(username);

        return token;
    }

    private Claims verifyTokenString(final String jwtString) throws InvalidTokenException {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwtString).getBody();
        } catch (SignatureException e) {
            throw new InvalidTokenException();
        }
    }

    private String createTokenString(final String userName) {
        final ZonedDateTime now = ZonedDateTime.now();

        return Jwts.builder().
                setSubject(userName).
                claim(CLAIM_USERNAME, userName).
                signWith(SIGNING_ALGORITHM, signingKey).
                setIssuedAt(Date.from(Instant.from(now))).
                setExpiration(Date.from(Instant.from(now.plus(expiryTime)))).
                compact();
    }
}
