package org.bsm.websocket;


import lombok.extern.slf4j.Slf4j;
import org.bsm.service.impl.SystemDetailInfoServiceImpl;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GZC
 * @create 2022-01-14 15:36
 * @desc 监控服务器的websocket
 */
@Slf4j
@Component
@ServerEndpoint(value = "/system/monitor")
public class SystemMonitorWebSocket {

    /*
     * 解决WebSocket不能注入的问题,原因是SpringBoot的注入对象是单例的，
     * 而websocket的对象会因每次连接而创建，导致注入的对象为null,这边在启动的时候把上下文传进去，
     * 在websocket中来获取SpringBean
     */

    SystemDetailInfoServiceImpl systemDetailInfoService = new SystemDetailInfoServiceImpl();

    /**
     * 记录当前在线连接数
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 存放所有在线的客户端
     */
    private static Map<String, Session> clients = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        onlineCount.incrementAndGet(); // 在线数加1
        clients.put(session.getId(), session);
        log.info("有新连接加入：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
        String s = systemDetailInfoService.getSystemDetailInfo().toJSONString();
        this.sendMessage(s, session);

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        onlineCount.decrementAndGet(); // 在线数减1
        clients.remove(session.getId());
        log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("服务端收到客户端[{}]的消息:{}", session.getId(), message);
        String s = systemDetailInfoService.getSystemDetailInfo().toJSONString();
        this.sendMessage(s, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 群发消息
     *
     * @param message 消息内容
     */
    private void sendMessage(String message, Session fromSession) {
        for (Map.Entry<String, Session> sessionEntry : clients.entrySet()) {
            Session toSession = sessionEntry.getValue();
            // 排除掉自己
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getAsyncRemote().sendText(message);
//            if (!fromSession.getId().equals(toSession.getId())) {
//            }
        }
    }
}
