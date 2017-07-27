package app.service;

import app.Config;
import app.data.Token;
import app.data.User;
import app.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.exparity.hamcrest.date.DateMatchers;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import java.security.Key;
import java.time.Instant;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class JwtTokenServiceTest {
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    private static final Key signingKey = MacProvider.generateKey(signatureAlgorithm);
    private static final TemporalAmount expiryTime = Period.ofDays(1);

    @Test
    public void generateTokenForUser() {
        Date expectedExpiryDate = Date.from(Instant.from(ZonedDateTime.now().plus(expiryTime)));

        TokenService service = new JwtTokenService(signingKey, signatureAlgorithm, expiryTime);

        User user = new User("joe@joe.com", Collections.singleton("user"));

        Token token = service.forUser(user);
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token.getToken()).getBody();

        assertEquals(user.getName(), claims.get("username"));
        assertEquals(user.getName(), claims.getSubject());
        assertThat(claims.getExpiration(), DateMatchers.within(2, ChronoUnit.SECONDS, expectedExpiryDate));
    }

    @Test
    public void generateTokenForJwtString() throws InvalidTokenException {
        TokenService service = new JwtTokenService(signingKey, signatureAlgorithm, expiryTime);
        User user = new User("joe@joe.com", Collections.singleton("user"));
        Token token = service.forUser(user);

        assertEquals(token, service.forJwtString(token.getToken()));
    }

    @Test(expected = InvalidTokenException.class)
    public void tokenMustContainValidClaims() throws InvalidTokenException {
        TokenService service = new JwtTokenService(signingKey, signatureAlgorithm, expiryTime);

        String token = buildJwtString().
                setClaims(Collections.singletonMap("outerclaim", "1")).
                compact();

        service.forJwtString(token);
    }

    @Test(expected = InvalidTokenException.class)
    public void tokenMustContainSubjectMatchingUsernameClaim() throws InvalidTokenException {
        TokenService service = new JwtTokenService(signingKey, signatureAlgorithm, expiryTime);

        String token = buildJwtString().setSubject("bar").compact();
        service.forJwtString(token);

        String tokenWithoutSubject = buildJwtString().setSubject(null).compact();
        service.forJwtString(tokenWithoutSubject);
    }

    @Test(expected = InvalidTokenException.class)
    public void tokensSignedWithOtherKeysShouldGetRejected() throws InvalidTokenException {
        TokenService service = new JwtTokenService(signingKey, signatureAlgorithm, expiryTime);

        Key otherKey = MacProvider.generateKey(signatureAlgorithm);

        String token = buildJwtString()
                .signWith(signatureAlgorithm, otherKey).compact();
        service.forJwtString(token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildFromEmptyConfigShouldFail() {
        TokenService ts = new JwtTokenService(new ResourceConfig());
        User user = new User("joe@example.com",
                Collections.singleton("user"));
        ts.forUser(user);
    }

    @Test
    public void buildFromConfiguration() {
        ResourceConfig rc = new ResourceConfig();
        rc.property(Config.JWT_KEY, signingKey);
        rc.property(Config.JWT_KEY_ALG, signatureAlgorithm);

        TokenService ts = new JwtTokenService(rc);

        User user = new User("joe@example.com",
                Collections.singleton("user"));
        Token token = ts.forUser(user);

        // verify the token service signed with the key with provided in the config
        Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token.getToken());
    }


    private JwtBuilder buildJwtString() {
        Date expiry = Date.from(Instant.from(ZonedDateTime.now().plus(expiryTime)));
        return Jwts.builder().
                setSubject("foo").
                claim("username", "foo").
                setIssuedAt(new Date()).
                setExpiration(expiry).
                signWith(signatureAlgorithm, signingKey);
    }
}
