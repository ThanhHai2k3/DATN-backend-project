package com.backend.jobservice.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
@Slf4j
public class JwtHeaderAuthenticationFilter implements Filter {

    @Value("${gateway.secret:}") // Shared secret giữa gateway và services
    private String gatewaySecret;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // ✅ Verify request từ gateway
//        String gatewaySignature = httpRequest.getHeader("X-Gateway-Signature");
//        if (!isValidGatewayRequest(gatewaySignature)) {
//            log.warn("Unauthorized direct access attempt from {}", httpRequest.getRemoteAddr());
//            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }

        String userId = httpRequest.getHeader("X-User-Id");
        String role = httpRequest.getHeader("X-User-Role");

        if (userId != null && role != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();

            // ✅ Support multiple roles
            String[] roles = role.split(",");
            for (String r : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r.trim()));
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated user {} with roles {}", userId, role);
        }

        chain.doFilter(request, response);
    }

    private boolean isValidGatewayRequest(String signature) {
        // Option 1: Shared secret
        return gatewaySecret.equals(signature);

        // Option 2: HMAC signature
        // return verifyHMAC(signature);

        // Option 3: mTLS (mutual TLS) - best but complex
    }
}
