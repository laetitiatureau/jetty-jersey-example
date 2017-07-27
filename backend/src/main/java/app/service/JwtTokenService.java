package app.service;

import app.Config;
import app.data.Token;
import app.data.User;
import app.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import java.security.Key;
import java.time.Instant;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.Objects;

public class JwtTokenService implements TokenService {
    private static final String CLAIM_USERNAME = "username";

    private final TemporalAmount expiryTime;
    private final Key signingKey;
    private final SignatureAlgorithm signatureAlgorithm;

    public JwtTokenService(@Context final Configuration config) {
        this.signingKey = (Key) config.getProperty(Config.JWT_KEY);
        this.signatureAlgorithm = (SignatureAlgorithm) config.getProperty(Config.JWT_KEY_ALG);
        this.expiryTime = Period.ofDays(1);
    }

    public JwtTokenService(final Key signingKey, final SignatureAlgorithm signatureAlgorithm, TemporalAmount expiryTime) {
        this.signingKey = signingKey;
        this.signatureAlgorithm = signatureAlgorithm;
        this.expiryTime = expiryTime;
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

        String subject = claims.getSubject();

        if (subject == null || !Objects.equals(subject, username)) {
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
                signWith(signatureAlgorithm, signingKey).
                setIssuedAt(Date.from(Instant.from(now))).
                setExpiration(Date.from(Instant.from(now.plus(expiryTime)))).
                compact();
    }
}
