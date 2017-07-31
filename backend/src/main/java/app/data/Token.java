package app.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Token {
    private final String token;

    @JsonIgnore
    private final String username;

    @JsonCreator
    public Token(@JsonProperty("token") String token, @JsonProperty("username") String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof Token)) {
            return false;
        } else {
            Token otherToken = (Token) other;
            return Objects.equals(token, otherToken.token) && Objects.equals(username, otherToken.username);
        }
    }

    public int hashCode() {
        return Objects.hash(token, username);
    }
}
