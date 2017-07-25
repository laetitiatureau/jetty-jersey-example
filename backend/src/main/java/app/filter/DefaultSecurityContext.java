package app.filter;

import app.data.User;

import javax.ws.rs.core.SecurityContext;

public class DefaultSecurityContext implements SecurityContext {
    private UserAdapter principal;

    public DefaultSecurityContext(User user) {
        this.principal = new UserAdapter(user);
    }

    @Override
    public UserAdapter getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        User user = principal.getUser();
        return user.getRoles().contains(role);
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.DIGEST_AUTH;
    }

    public static class UserAdapter implements java.security.Principal {
        private final User user;

        public UserAdapter(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        @Override
        public String getName() {
            return user.getName();
        }
    }
}
