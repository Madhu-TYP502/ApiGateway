package com.tyss.dashboard.api.gateway.config;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class Routevalidator {

	public static final List<String> openApiEndpoints= List.of(
            "/users/status",
            "/auth/login",
            "users/login",
            "/v2/api-docs",
            "/swagger-ui.html"
    );
	
	public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}

