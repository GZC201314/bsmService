package org.bsm.websocket;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.bsm.service.impl.SystemDetailInfoServiceImpl;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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


    /**
     * 记录当前在线连接数
     */
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
    /**
     * 存放所有在线的客户端
     */
    private static final Map<String, Session> CLIENTS = new ConcurrentHashMap<>();

    private static SystemDetailInfoServiceImpl systemDetailInfoService;

    @Autowired
    public void setSystemDetailInfoService(SystemDetailInfoServiceImpl systemDetailInfoService) {
        SystemMonitorWebSocket.systemDetailInfoService = systemDetailInfoService;
    }

    private static RedisUtil redisUtil;

    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        SystemMonitorWebSocket.redisUtil = redisUtil;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        ONLINE_COUNT.incrementAndGet(); // 在线数加1
        CLIENTS.put(session.getId(), session);
        log.info("有新连接加入：{}，当前在线人数为：{}", session.getId(), ONLINE_COUNT.get());

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                JSONObject systemDetailInfo = systemDetailInfoService.getSystemDetailInfo();
                // 拼装当前在线人物
                systemDetailInfo.put("curUserCount", ONLINE_COUNT.get());
                // 获取当前访问成功最频繁的接口
                Set<ZSetOperations.TypedTuple<Object>> qpsSuccess1 = redisUtil.getZsetMaxKeysOfScores("QPS_success", 0, 9);
                List<JSONObject> topSuccessRequest = getTopList(qpsSuccess1);
                systemDetailInfo.put("topSuccessRequest", topSuccessRequest);

                Set<ZSetOperations.TypedTuple<Object>> qpsFail = redisUtil.getZsetMaxKeysOfScores("QPS_fail", 0, 10);
                List<JSONObject> topFailRequest = getTopList(qpsFail);
                systemDetailInfo.put("topFailRequest", topFailRequest);

                String s = systemDetailInfo.toJSONString();

                session.getAsyncRemote().sendText(s);
            }
        };
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("systemmonitor-schedule-pool-%d").daemon(true).build());

        executor.scheduleWithFixedDelay(timerTask, 0, 30, TimeUnit.SECONDS);

    }

    private List<JSONObject> getTopList(Set<ZSetOperations.TypedTuple<Object>> qpsSuccess1) {
        List<JSONObject> topSuccessRequest = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> qpsSuccess : qpsSuccess1) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", qpsSuccess.getValue());
            jsonObject.put("score", qpsSuccess.getScore());
            topSuccessRequest.add(jsonObject);
        }
        return topSuccessRequest;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        ONLINE_COUNT.decrementAndGet(); // 在线数减1
        CLIENTS.remove(session.getId());
        log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), ONLINE_COUNT.get());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("服务端收到客户端[{}]的消息[{}]", session.getId(), message);
        String s = systemDetailInfoService.getSystemDetailInfo().toJSONString();
        sendMessage(s, session);

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("客户端[{}]发生错误", session.getId());
        error.printStackTrace();
    }

    /**
     * 群发消息
     *
     * @param message 消息内容
     */
    private void sendMessage(String message, Session fromSession) {
        for (Map.Entry<String, Session> sessionEntry : CLIENTS.entrySet()) {
            Session toSession = sessionEntry.getValue();
            // 排除掉自己
            log.info("服务端[{}]给客户端[{}]发送消息", fromSession.getId(), toSession.getId());
            toSession.getAsyncRemote().sendText(message);
        }
    }
}
