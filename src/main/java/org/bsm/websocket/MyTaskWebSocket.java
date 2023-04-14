package org.bsm.websocket;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author GZC
 * @create 2022-01-14 15:36
 * @desc 监控服务器的websocket
 */
@Slf4j
@Component
@ServerEndpoint(value = "/mytask/{username}")
public class MyTaskWebSocket {
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    private static final Map<String, ScheduledFuture<?>> TASKS = new ConcurrentHashMap<>();
    /*
     * 解决WebSocket不能注入的问题,原因是SpringBoot的注入对象是单例的，
     * 而websocket的对象会因每次连接而创建，导致注入的对象为null,这边在启动的时候把上下文传进去，
     * 在websocket中来获取SpringBean
     */

    private static ProcessEngine processEngine;

    @Autowired
    public void setProcessEngine(ProcessEngine processEngine) {
        MyTaskWebSocket.processEngine = processEngine;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {

        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("username,{}", username);
            try {
                List<Task> list = processEngine.getTaskService().createTaskQuery().taskAssignee(username).orderByTaskDueDate().asc().list();
                JSONObject jsonObject = new JSONObject();
                List<JSONObject> taskList = new ArrayList<>();
                for (Task task : list) {
                    JSONObject taskJson = new JSONObject();
                    taskJson.put("description", task.getDescription());
                    taskJson.put("dueDate", task.getDueDate());
                    taskJson.put("assignee", task.getAssignee());
                    taskJson.put("claimTime", task.getClaimTime());
                    taskJson.put("createTime", task.getCreateTime());
                    taskJson.put("parentTaskId", task.getParentTaskId());
                    taskJson.put("processDefinitionId", task.getProcessDefinitionId());
                    taskJson.put("processVariables", task.getProcessVariables());
                    taskJson.put("executionId", task.getExecutionId());
                    taskList.add(taskJson);
                }
                jsonObject.put("taskList", taskList);
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(jsonObject.toJSONString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 30, TimeUnit.SECONDS);

        TASKS.put(session.getId(), scheduledFuture);

    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        ScheduledFuture<?> scheduledFuture = TASKS.get(session.getId());
        scheduledFuture.cancel(true);
        TASKS.remove(session.getId());
        log.info("mytask session.getId() = " + session.getId());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("客户端[{}]发生错误", session.getId());
        error.printStackTrace();
    }
}
