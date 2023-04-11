package org.bsm.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.CurUser;
import org.bsm.pagemodel.PageFlow;
import org.bsm.service.IFlowableService;
import org.bsm.utils.ObjectUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

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
    public JSONObject getFlowList(PageFlow pageFlow) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().active().orderByProcessDefinitionId().asc().list();

        JSONObject jsonObject = new JSONObject();
        if (CollectionUtils.isEmpty(list)) {
            jsonObject.put("current", 1);
            jsonObject.put("total", 0);
            jsonObject.put("size", 10);
            jsonObject.put("pages", 1);
            jsonObject.put("records", new ArrayList<>());
            return jsonObject;
        } else {
            int totalSize;
            int totalPage;
            // 计算总页数
            totalSize = list.size();
            Integer pageSize = pageFlow.getPage().getPageSize();
            totalPage = PageUtil.totalPage(totalSize, pageSize);
            Integer curPage = pageFlow.getPage().getPage();
            // 分页，索引小于等于总页数，才返回列表.
            List<JSONObject> records = new ArrayList<>();
            if (curPage <= totalPage) {
                // 分页
                list = CollUtil.page(curPage - 1, pageSize, list);
                handleFlowList(repositoryService, list, records);
            }
            // 返回结果
            jsonObject.put("current", curPage);
            jsonObject.put("total", totalSize);
            jsonObject.put("size", pageSize);
            jsonObject.put("pages", totalPage);
            jsonObject.put("records", records);
        }
        return jsonObject;
    }

    @Override
    public JSONObject getFlowFormByFlowId(PageFlow pageFlow) {
        JSONObject flowForm = new JSONObject();
        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(pageFlow.getId());
        Process mainProcess = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            Map<String, List<ExtensionElement>> extensionElements = flowElement.getExtensionElements();
            List<ExtensionElement> formData = extensionElements.get("formData");
            if (CollUtil.isNotEmpty(formData)) {
                ExtensionElement extensionElement = formData.get(0);
                Map<String, List<ExtensionElement>> childElements = extensionElement.getChildElements();
                for (Map.Entry<String, List<ExtensionElement>> stringListEntry : childElements.entrySet()) {
                    handleForm(flowForm, stringListEntry);
                }
            }
        }
        return flowForm;
    }

    private void handleForm(JSONObject flowForm, Map.Entry<String, List<ExtensionElement>> stringListEntry) {
        if ("formField".equals(stringListEntry.getKey())) {
            List<ExtensionElement> value = stringListEntry.getValue();

            List<JSONObject> formList = new ArrayList<>();
            for (ExtensionElement element : value) {
                Map<String, List<ExtensionAttribute>> attributes = element.getAttributes();
                JSONObject formDetail = new JSONObject();
                for (List<ExtensionAttribute> extensionAttributes : attributes.values()) {
                    for (ExtensionAttribute extensionAttribute : extensionAttributes) {
                        formDetail.put(extensionAttribute.getName(), extensionAttribute.getValue());
                    }
                }
                formList.add(formDetail);
            }
            flowForm.put("form", formList);
        }
    }

    @Override
    public JSONObject getMyApplicationList(PageFlow pageFlow, CurUser curUser) {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().taskOwner(curUser.getUserid()).orderByTaskCreateTime().desc().list();
        JSONObject jsonObject = new JSONObject();
        if (CollectionUtils.isEmpty(list)) {
            jsonObject.put("current", 1);
            jsonObject.put("total", 0);
            jsonObject.put("size", 10);
            jsonObject.put("pages", 1);
            jsonObject.put("records", new ArrayList<>());
            return jsonObject;
        } else {
            int totalSize;
            int totalPage;
            // 计算总页数
            totalSize = list.size();
            Integer pageSize = pageFlow.getPage().getPageSize();
            totalPage = PageUtil.totalPage(totalSize, pageSize);
            Integer curPage = pageFlow.getPage().getPage();
            // 分页，索引小于等于总页数，才返回列表.
            List<JSONObject> records = new ArrayList<>();
            if (curPage <= totalPage) {
                // 分页
                list = CollUtil.page(curPage - 1, pageSize, list);
                handleMyApplicationList(list, records);
            }
            // 返回结果

            jsonObject.put("current", curPage);
            jsonObject.put("total", totalSize);
            jsonObject.put("size", pageSize);
            jsonObject.put("pages", totalPage);
            jsonObject.put("records", records);
        }
        return jsonObject;
    }

    @Override
    public List<JSONObject> getAllFlow(PageFlow pageFlow) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<ProcessDefinition> list;
        if (StringUtils.hasText(pageFlow.getKey())) {
            list = repositoryService.createProcessDefinitionQuery().active().processDefinitionKeyLike(ObjectUtils.convertToLike(pageFlow.getKey())).orderByProcessDefinitionId().asc().list();
        } else {
            list = repositoryService.createProcessDefinitionQuery().active().orderByProcessDefinitionId().asc().list();
        }
        List<JSONObject> records = new ArrayList<>();
        handleFlowList(repositoryService, list, records);
        // 返回结果
        return records;
    }


    private void handleFlowList(RepositoryService repositoryService, List<ProcessDefinition> list, List<JSONObject> records) {
        for (ProcessDefinition processDefinition : list) {
            JSONObject json = new JSONObject();
            json.put("id", processDefinition.getId());
            json.put("name", processDefinition.getName());
            json.put("version", processDefinition.getVersion());
            json.put("description", processDefinition.getDescription());
            json.put("key", processDefinition.getKey());
            json.put("resourceName", processDefinition.getResourceName());
            json.put("hasImg", processDefinition.hasGraphicalNotation());
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            json.put("createtime", deployment.getDeploymentTime());
            records.add(json);
        }
    }

    private void handleMyApplicationList(List<Task> list, List<JSONObject> records) {
        for (Task task : list) {
            JSONObject json = new JSONObject();
            json.put("id", task.getId());
            json.put("name", task.getName());
            json.put("taskDefinitionKey", task.getTaskDefinitionKey());
            json.put("taskLocalVariables", task.getTaskLocalVariables());
            json.put("createTime", task.getCreateTime());
            json.put("description", task.getDescription());
            json.put("processVariables", task.getProcessVariables());
            records.add(json);
        }
    }

    @Override
    public boolean deployFlow(PageFlow pageFlow) {
        // 保存流程文件到本地
        Deployment deployment = processEngine.getRepositoryService().createDeployment().addString(pageFlow.getFlowName() + ".bpmn20.xml", pageFlow.getXml()).deploy();
        return !Objects.isNull(deployment);
    }

    @Override
    public JSONObject processInstance(PageFlow pageFlow) {
        JSONObject taskJson = new JSONObject();
        try {
            RuntimeService runtimeService = processEngine.getRuntimeService();
            TaskService taskService = processEngine.getTaskService();
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(pageFlow.getId());
            String processInstanceId = processInstance.getProcessInstanceId();
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
            if (getTaskDetail(taskJson, task, processInstance.getProcessDefinitionId())) {
                return taskJson;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return taskJson;
        }
        return taskJson;
    }

    @Override
    public JSONObject taskDetail(PageFlow pageFlow) {
        JSONObject taskJson = new JSONObject();
        try {
            TaskService taskService = processEngine.getTaskService();
            Task task = taskService.createTaskQuery().taskId(pageFlow.getId()).singleResult();
            if (getTaskDetail(taskJson, task, task.getProcessDefinitionId())) {
                return taskJson;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return taskJson;
        }
        return taskJson;
    }

    private boolean getTaskDetail(JSONObject taskJson, Task task, String processDefinitionId) {
        taskJson.put("formKey", task.getFormKey());
        taskJson.put("name", task.getName());
        taskJson.put("processVariables", task.getProcessVariables());
        taskJson.put("taskLocalVariables", task.getTaskLocalVariables());
        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(processDefinitionId);
        Process mainProcess = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (task.getTaskDefinitionKey().equals(flowElement.getId())) {
                Map<String, List<ExtensionElement>> extensionElements = flowElement.getExtensionElements();
                List<ExtensionElement> formData = extensionElements.get("formData");
                if (CollUtil.isNotEmpty(formData)) {
                    ExtensionElement extensionElement = formData.get(0);
                    Map<String, List<ExtensionElement>> childElements = extensionElement.getChildElements();
                    for (Map.Entry<String, List<ExtensionElement>> stringListEntry : childElements.entrySet()) {
                        handleForm(taskJson, stringListEntry);
                        return true;
                    }

                }
            }

        }
        return false;
    }

    @Override
    public boolean deleteFlows(PageFlow pageFlow) {
        String delIds = pageFlow.getDelIds();
        if (!StringUtils.hasText(delIds)) {
            return false;
        }
        String[] flowKeys = delIds.split(",");
        for (String flowKey : flowKeys) {
            processEngine.getRepositoryService().suspendProcessDefinitionByKey(flowKey);
        }
        return true;
    }

    @Override
    public void getFlowImg(String id, HttpServletResponse response) throws IOException {
        OutputStream os = null;
        InputStream processDiagram = null;
        try {
            RepositoryService repositoryService = processEngine.getRepositoryService();
            processDiagram = repositoryService.getProcessDiagram(id);
            response.setContentLength(processDiagram.available());
            byte[] data = IoUtil.readInputStream(processDiagram, "flow diagram");
            String diskfilename = id + ".png";
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + diskfilename + "\"");
            os = response.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!Objects.isNull(os)) {
                os.close();
            }
            if (!Objects.isNull(processDiagram)) {
                processDiagram.close();
            }
        }

    }

    @Override
    public void getTaskFlowDiagram(String procInsId, HttpServletResponse response) throws IOException {

        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        HistoryService historyService = processEngine.getHistoryService();
        String procDefId;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInsId)
                .singleResult();
        if (processInstance == null) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
            procDefId = historicProcessInstance.getProcessDefinitionId();

        } else {
            procDefId = processInstance.getProcessDefinitionId();
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
        // 创建默认的流程图生成器
        DefaultProcessDiagramGenerator defaultProcessDiagramGenerator = new DefaultProcessDiagramGenerator();
        // 生成图片的类型
        String imageType = "png";
        // 高亮节点集合
        List<String> highLightedActivities = new ArrayList<>();
        // 高亮连线集合
        List<String> highLightedFlows = new ArrayList<>();
        // 查询所有历史节点信息
        List<HistoricActivityInstance> hisActInsList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInsId)
                .list();
        // 遍历
        hisActInsList.forEach(historicActivityInstance -> {
            if ("sequenceFlow".equals(historicActivityInstance.getActivityType())) {
                // 添加高亮连线
                highLightedFlows.add(historicActivityInstance.getActivityId());
            } else {
                // 添加高亮节点
                highLightedActivities.add(historicActivityInstance.getActivityId());
            }
        });
        // 节点字体
        String activityFontName = "宋体";
        // 连线标签字体
        String labelFontName = "宋体";
        // 连线标签字体
        String annotationFontName = "宋体";
        // 类加载器
        ClassLoader customClassLoader = null;
        // 比例因子，默认即可
        double scaleFactor = 1.0d;
        // 不设置连线标签不会画
        boolean drawSequenceFlowNameWithNoLabelDI = true;
        // 生成图片
        // 获取输入流
        InputStream inputStream = defaultProcessDiagramGenerator.generateDiagram(bpmnModel, imageType, highLightedActivities
                , highLightedFlows, activityFontName, labelFontName, annotationFontName, customClassLoader,
                scaleFactor, drawSequenceFlowNameWithNoLabelDI);

        // 直接写到页面，要先获取HttpServletResponse
        byte[] bytes = IoUtil.readInputStream(inputStream, "flow diagram");
        response.setContentType("image/png");
        ServletOutputStream outputStream = response.getOutputStream();
        response.reset();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }


}
