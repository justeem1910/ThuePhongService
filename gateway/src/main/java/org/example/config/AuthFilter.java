package org.example.config;

import org.example.dto.AuthorityDto;
import org.example.dto.ErrorDto;
import org.example.dto.UserDto;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode; // Import cái này
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Kiểm tra header Authorization
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return Mono.error(new RuntimeException("Missing authorization information"));
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                return Mono.error(new RuntimeException("Incorrect authorization structure"));
            }

            return webClientBuilder.build()
                    .post()
                    .uri("http://user-service/user/validateToken?token=" + parts[1])
                    .header("Authorization", authHeader) // Gửi lại header gốc
                    .retrieve()
                    // --- SỬA Ở ĐÂY ---
                    // 1. Dùng lambda status -> status.is4xx... thay vì method reference
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(ErrorDto.class)
                                    .flatMap(error -> {
                                        // 2. Kiểm tra lại getter (thường là getErrorMessage)
                                        // Nếu class ErrorDto của bạn thực sự tên là getError_message thì giữ nguyên
                                        return Mono.error(new RuntimeException(error.getError_message()));
                                    })
                    )
                    // Tương tự cho lỗi 5xx nếu cần
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new RuntimeException("Server error from User Service"))
                    )
                    // -----------------
                    .bodyToMono(UserDto.class)
                    .map(userDto -> {
                        exchange.getRequest().mutate()
                                .header("userId", String.valueOf(userDto.getUserId())) // Chuyển sang String cho an toàn
                                .header("authorities", userDto.getAuthorities().stream()
                                        .map(AuthorityDto::getAuthority)
                                        .reduce("", (a, b) -> a.isEmpty() ? b : a + "," + b)) // Fix lỗi reduce sinh dấu phẩy thừa
                                .header("username", userDto.getUsername())
                                .build();
                        return exchange;
                    })
                    .flatMap(chain::filter);
        };
    }

    public static class Config {
    }
}