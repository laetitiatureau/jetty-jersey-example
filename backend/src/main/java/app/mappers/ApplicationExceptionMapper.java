package app.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<Exception> {
    private final Logger logger = Logger.getGlobal();
    @Override
    public Response toResponse(Exception exception) {
        logger.log(Level.SEVERE, "Internal server error", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
