package app.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Token {
    private String token;

    @JsonIgnore
    private String username;

    public Token() {
        // empty constructor for jackson
    }

    public Token(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
