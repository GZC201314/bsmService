package org.bsm.websocket;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.bsm.service.impl.SystemDetailInfoServiceImpl;
import org.bsm.utils.RedisUtil;
import org.flowable.engine.ProcessEngine;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
@ServerEndpoint(value = "/mytask/{username}")
public class MyTaskWebSocket {

    private static final ScheduledThreadPoolExecutor EXECUTOR;
    private static final Map<String, TimerTask> TASKS = new ConcurrentHashMap<>();
    /*
     * 解决WebSocket不能注入的问题,原因是SpringBoot的注入对象是单例的，
     * 而websocket的对象会因每次连接而创建，导致注入的对象为null,这边在启动的时候把上下文传进去，
     * 在websocket中来获取SpringBean
     */

    static {
        EXECUTOR = new ScheduledThreadPoolExecutor(10,
                new BasicThreadFactory.Builder().namingPattern("mytaskschedule-pool-%d").daemon(true).build());
    }


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
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                log.info("username,{}",username);
                try {
                    List<Task> list = processEngine.getTaskService().createTaskQuery().taskAssignee(username).orderByTaskDueDate().asc().list();
                    JSONObject jsonObject = new JSONObject();
                    List<JSONObject> taskList = new ArrayList<>();
                    for (Task task : list) {
                        JSONObject taskJson = new JSONObject();
                        taskJson.put("description",task.getDescription());
                        taskJson.put("dueDate",task.getDueDate());
                        taskJson.put("assignee",task.getAssignee());
                        taskJson.put("claimTime",task.getClaimTime());
                        taskJson.put("createTime",task.getCreateTime());
                        taskJson.put("parentTaskId",task.getParentTaskId());
                        taskJson.put("processDefinitionId",task.getProcessDefinitionId());
                        taskJson.put("processVariables",task.getProcessVariables());
                        taskList.add(taskJson);
                    }
                    jsonObject.put("taskList",taskList);
                    session.getAsyncRemote().sendText(jsonObject.toJSONString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        TASKS.put(session.getId(), timerTask);
        EXECUTOR.scheduleWithFixedDelay(timerTask, 0, 30, TimeUnit.SECONDS);

    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        TimerTask timerTask = TASKS.get(session.getId());
        EXECUTOR.remove(timerTask);
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
