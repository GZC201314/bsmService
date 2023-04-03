package org.bsm.interceptor;

import org.bsm.utils.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author GZC
 * @create 2023-01-15 14:28
 * @desc
 */
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    @Resource
    RedisUtil redisUtil;

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
//websocket-security-token sec-websocket-protocol -> {ArrayList@18837}  size = 1
//        List<String> strings = serverHttpRequest.getHeaders().get("sec-websocket-protocol");
//        return !Objects.isNull(strings) && "websocket-security-token".equals(strings.get(0));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
