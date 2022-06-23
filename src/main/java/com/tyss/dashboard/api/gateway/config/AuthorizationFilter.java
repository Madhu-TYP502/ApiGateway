package com.tyss.dashboard.api.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthorizationFilter implements GlobalFilter {

	Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

	@Autowired
	private Routevalidator routerValidator;// custom route validator

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		logger.info("INSIDE filter");

		ServerHttpRequest request = exchange.getRequest();

		if (routerValidator.isSecured.test(request)) {
			logger.info("ROUTE IS SECURED :  NEED TOKEN TO ACCESS");

			if (request.getHeaders().get("Authorization") != null) {
				logger.info("Authorization header is present: " + request.getHeaders().get("Authorization").get(0));
				

				if (jwtUtil.isInvalid(request.getHeaders().get("Authorization").get(0).replace("Bearer","").trim()))
				{
					logger.info("Authorization header is invalid");
				    return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
				}
			} else {
				return onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
			}

		} else {
			logger.info("ROUTE IS NOT SECURED : DONT REQUIRE TOKEN");
		}
		return chain.filter(exchange);
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {

		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		return response.setComplete();

	}

	private String getAuthHeader(ServerHttpRequest request) {
		return request.getHeaders().getOrEmpty("Authorization").get(0);
	}

	private boolean isAuthMissing(ServerHttpRequest request) {
		return !request.getHeaders().containsKey("Authorization");
	}

}
