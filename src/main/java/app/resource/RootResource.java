package app.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.InputStream;

@Path("/")
public class RootResource {
    @GET
    public InputStream getIndex() {
        return getClass().getResourceAsStream("/static/index.html");
    }
}

