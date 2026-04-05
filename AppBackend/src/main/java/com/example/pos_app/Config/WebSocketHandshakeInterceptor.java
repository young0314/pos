package com.example.pos_app.Config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        System.out.println("WebSocket handshake 요청 들어옴");
        System.out.println("URI = " + request.getURI());
        System.out.println("Headers = " + request.getHeaders());

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        System.out.println("WebSocket handshake 완료");
        if (exception != null) {
            System.out.println("handshake exception = " + exception.getMessage());
        }
    }
}