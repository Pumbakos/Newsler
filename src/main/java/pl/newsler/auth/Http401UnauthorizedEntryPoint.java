package pl.newsler.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {
    /**
     * Always returns a 401 error code to the client.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.debug("Pre-authenticated entry point called. Unauthorized access");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Pre-unauthorized");
    }

    /**
     * Handles an access denied failure.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException      in the event of an IOException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.debug("Pre-authenticated entry point called. Unauthorized access\n{}", accessDeniedException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Pre-unauthorized");
    }
}