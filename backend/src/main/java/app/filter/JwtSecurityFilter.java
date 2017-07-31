package app.filter;

import app.Config;
import app.data.Token;
import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.InvalidTokenException;
import app.service.JwtTokenService;
import app.service.DummyUserService;
import app.service.TokenService;
import app.service.UserService;
import org.glassfish.jersey.server.ContainerRequest;

import javax.annotation.Priority;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Provider
@Singleton
@Priority(Priorities.AUTHENTICATION)
public class JwtSecurityFilter implements ContainerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;

    public JwtSecurityFilter(@Context Configuration config) {
        this.userService = new DummyUserService((File) config.getProperty(Config.CONFDIR));
        this.tokenService = new JwtTokenService(config);
    }

    public JwtSecurityFilter(final TokenService tokenService, final UserService userService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        requestContext.setSecurityContext(createSecurityContext(requestContext));
    }

    private SecurityContext createSecurityContext(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        String method = requestContext.getMethod().toUpperCase();
        String path = ((ContainerRequest) requestContext).getPath(true);

        if (HttpMethod.POST.equals(method) && "auth".equals(path)) {
            return new DefaultSecurityContext(() -> "anonymous", Collections.emptySet());
        }

        if (HttpMethod.OPTIONS.equals(method)) {
            throw new WebApplicationException(createOptionsResponse());
        }

        if (authHeader == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        Token token = tokenFromAuthHeader(authHeader);
        return securityContextFromToken(token);
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
