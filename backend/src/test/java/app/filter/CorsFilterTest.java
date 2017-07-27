package app.filter;

import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CorsFilterTest {
    @Test
    public void filterAddsAllExpectedHeaders() throws IOException {
        CORSFilter filter = new CORSFilter();
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
        when(responseContext.getHeaders()).thenReturn(headers);
        filter.filter(requestContext, responseContext);

        assertThat(headers, hasKey("Access-Control-Allow-Credentials"));
        assertThat(headers, hasKey("Access-Control-Allow-Origin"));
        assertThat(headers, hasKey("Access-Control-Allow-Methods"));
        assertThat(headers, hasKey("Access-Control-Allow-Headers"));
    }

}
