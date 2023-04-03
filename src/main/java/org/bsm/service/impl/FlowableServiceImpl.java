package org.bsm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageFlow;
import org.bsm.service.IFlowableService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GZC
 * @create 2022-02-17 15:24
 * @desc 定时任务实现类
 */
@Slf4j
@Service
public class FlowableServiceImpl implements IFlowableService {

    @Resource
    private ProcessEngine processEngine;

    @Override
    public List<JSONObject> getFlowList() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        List<JSONObject> result = new ArrayList<>();
        for (ProcessDefinition processDefinition : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",processDefinition.getId());
            jsonObject.put("name",processDefinition.getName());
            jsonObject.put("version",processDefinition.getVersion());
            jsonObject.put("description",processDefinition.getDescription());
            jsonObject.put("key",processDefinition.getKey());
            jsonObject.put("resourceName",processDefinition.getResourceName());
            result.add(jsonObject);
        }
        return result;
    }

    @Override
    public boolean deployFlow(PageFlow pageFlow) {
        // 保存流程文件到本地

        return false;
    }
//
//    @Resource
//    private RuntimeService runtimeService;
//
//    @Resource
//    private TaskService taskService;
//
//
//
//
//
//    @Transactional
//    public void startProcess() {
//        runtimeService.startProcessInstanceByKey("oneTaskProcess");
//    }
//
//    @Transactional
//    public List<Task> getTasks(String assignee) {
//        return taskService.createTaskQuery().taskAssignee(assignee).list();
//    }


}
