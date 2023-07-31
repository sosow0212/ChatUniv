package mju.chatuniv.global.config.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter implements Filter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_ = "Bearer ";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        if (isLoginURI(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader(AUTHORIZATION);

        if (token == null || !token.startsWith(BEARER_)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 존재하지 않습니다.");
            return;
        }

        // 토큰이 유효하지 않은 경우 접근 차단
        if (!isValidToken(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 유효하지 않습니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLoginURI(final String requestURI) {
        return requestURI.startsWith("/api/auth");
    }

    // 토큰 유효성을 확인하기 위한 로직을 구현
    private boolean isValidToken(String token) {
        return true;
    }

}
