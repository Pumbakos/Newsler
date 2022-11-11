package pl.newsler.security.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.security.NLIPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
class RequestInterceptor implements HandlerInterceptor {
    private final NLIPasswordEncoder passwordEncoder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getUserPrincipal() == null) {
            throw new UnauthorizedException("preHandle", "preHandle");
        }

        if (!request.isTrailerFieldsReady()) {
            return false;
        }
        request.getTrailerFields().values().forEach(passwordEncoder::decrypt);
        return true;
    }
}
