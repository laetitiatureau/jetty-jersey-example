package app.filter;

import app.data.Token;
import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.InvalidTokenException;
import app.service.*;
import org.glassfish.jersey.server.ContainerRequest;

import javax.annotation.Priority;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtSecurityFilter implements ContainerRequestFilter {

    private TokenService tokenService = new DefaultTokenService();
    private UserService userService = new DummyUserService();

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        requestContext.setSecurityContext(createSecurityContext(requestContext));
    }

    private SecurityContext createSecurityContext(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        String method = requestContext.getMethod().toUpperCase();
        String path = ((ContainerRequest) requestContext).getPath(true);

        if (HttpMethod.POST.equals(method) && "auth".equals(path)) {
            // allow access to auth resource so that it's possible to log in
            final User user = userService.getAnonymousUser();
            return new DefaultSecurityContext(user::getName, user.getRoles());
        } else if (HttpMethod.OPTIONS.equals(method)) {
            throw new WebApplicationException(createOptionsResponse());
        } else if (authHeader != null) {
            Token token = tokenFromAuthHeader(authHeader);
            return securityContextFromToken(token);
        } else {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    private Token tokenFromAuthHeader(final String authHeader) {
        if (!authHeader.toLowerCase().startsWith("bearer ")) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        String jwtString = authHeader.replaceFirst("^[B|b][E|e][A|a][R|r][E|e][R|r]", "").trim();

        try {
            return tokenService.forJwtString(jwtString);
        } catch (InvalidTokenException e) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    private SecurityContext securityContextFromToken(final Token token) {
        try {
            final User user = userService.getUser(token.getUsername());
            return new DefaultSecurityContext(user::getName, user.getRoles());
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    private Response createOptionsResponse() {
        // needed to get cors-requests with authorization header to work - the browser will send
        // a pre-flight 'OPTIONS' request and 'say it's ok to send the real request'

        String accept = "GET,PUT,DELETE,OPTIONS";
        return Response.ok().header(HttpHeaders.ACCEPT, accept).build();
    }


    public static class DefaultSecurityContext implements SecurityContext {
        private Principal principal;
        private Set<String> roles;

        DefaultSecurityContext(final Principal principal, final Set<String> roles) {
            this.principal = principal;
            this.roles = new HashSet<>(roles);
        }

        @Override
        public Principal getUserPrincipal() {
            return principal;
        }

        @Override
        public boolean isUserInRole(String role) {
            return roles.contains(role);
        }

        @Override
        public boolean isSecure() {
            return true;
        }

        @Override
        public String getAuthenticationScheme() {
            return SecurityContext.DIGEST_AUTH;
        }
    }
}
