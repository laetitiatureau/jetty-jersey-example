package app.data;

import java.util.Objects;

public class Token {
    private String authToken;

    public Token() {
        // empty constructor for moxy
    }

    public Token(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof Token)) {
            return false;
        } else {
            Token otherToken = (Token) other;
            return Objects.equals(authToken, otherToken.authToken);
        }
    }

    public int hashCode() {
        return Objects.hash(authToken);
    }
}
