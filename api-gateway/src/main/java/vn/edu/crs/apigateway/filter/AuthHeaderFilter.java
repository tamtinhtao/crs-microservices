package vn.edu.crs.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // =========================
        // PUBLIC: LOGIN
        // =========================
        if (path.equals("/api/auth/login")) {
            return chain.filter(exchange);
        }

        // =========================
        // PUBLIC: API PARTNER
        // ApiKeyFilter sẽ kiểm tra X-API-KEY
        // =========================
        if (path.startsWith("/api/public/courses")) {
            return chain.filter(exchange);
        }

        // =========================
        // PUBLIC: GET COURSES
        // Chỉ GET được public
        // POST/PUT/DELETE vẫn cần JWT
        // =========================
        if (HttpMethod.GET.equals(method)
                && path.startsWith("/api/courses")) {
            return chain.filter(exchange);
        }

        // =========================
        // CÁC REQUEST CÒN LẠI
        // PHẢI CÓ AUTHORIZATION
        // =========================
        String authorizationHeader =
                request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null
                || authorizationHeader.isBlank()) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}