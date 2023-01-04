package pl.newsler.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {
    /**
     * Always returns a 403 error code to the client.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.debug("Pre-authenticated entry point called. Unauthorized access");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Pre-unauthorized");
    }

}