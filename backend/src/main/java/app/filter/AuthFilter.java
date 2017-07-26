package app.filter;

import app.data.User;
import com.sun.research.ws.wadl.HTTPMethods;
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
import java.util.Collections;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        SecurityContext securityContext = createSecurityContext(requestContext);

        if (securityContext != null) {
            requestContext.setSecurityContext(securityContext);
        } else {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    private SecurityContext createSecurityContext(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        String method = requestContext.getMethod().toUpperCase();
        String path = ((ContainerRequest) requestContext).getPath(true);

        if (HttpMethod.POST.equals(method) && "auth".equals(path)) {
            User user = new User();
            user.setRoles(Collections.emptySet());
            user.setName("anonymous");
            user.setVersion(0);
            return new DefaultSecurityContext(user);
        } else if (HttpMethod.OPTIONS.equals(method)) {
            String accept = "GET,PUT,DELETE,OPTIONS";
            // needed to get cors-requests with authorization header to work - the browser will send
            // a preflight 'OPTIONS' request and 'say it's ok to send the real request'
            throw new WebApplicationException(Response.ok().header(HttpHeaders.ACCEPT, accept).build());
        } else if (authHeader != null) {
            // add jwt magic
            User user = new User();
            user.setRoles(Collections.singleton("user"));
            user.setName("joe@example.com");
            user.setVersion(0);
            return new DefaultSecurityContext(user);
        } else {
            // unauthorized
            return null;
        }
    }
}
