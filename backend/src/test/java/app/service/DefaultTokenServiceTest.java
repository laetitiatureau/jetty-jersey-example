package app.service;

import app.data.Token;
import app.data.User;
import app.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.exparity.hamcrest.date.DateMatchers;
import org.junit.Test;

import java.security.Key;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;

public class DefaultTokenServiceTest {

    @Test
    public void generateTokenForUser() {
        Key key = MacProvider.generateKey();

        TemporalAmount expiryTime = Period.ofDays(1);
        Date expectedExpiryDate = Date.from(Instant.from(ZonedDateTime.now().plus(expiryTime)));

        TokenService service = new DefaultTokenService(key, expiryTime);

        User user = new User("joe@joe.com",Collections.singleton("user"));

        Token token = service.forUser(user);
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token.getToken()).getBody();

        assertEquals(user.getName(), claims.get("username"));
        assertEquals(user.getName(), claims.getSubject());
        assertThat(claims.getExpiration(), DateMatchers.within(2, ChronoUnit.SECONDS, expectedExpiryDate));
    }

    @Test
    public void generateTokenForJwtString() throws InvalidTokenException {
        TokenService service = new DefaultTokenService();
        User user = new User("joe@joe.com",Collections.singleton("user"));
        Token token = service.forUser(user);

        assertEquals(token, service.forJwtString(token.getToken()));
    }
}
