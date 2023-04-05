package org.bsm.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageFlow;
import org.bsm.service.IFlowableService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public boolean deployFlow(PageFlow pageFlow) {
        // 保存流程文件到本地
        Deployment deployment = processEngine.getRepositoryService().createDeployment().addString(pageFlow.getFlowName() + ".bpmn20.xml", pageFlow.getXml()).deploy();
        return !Objects.isNull(deployment);
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
            byte[] data = new byte[processDiagram.available()];
            processDiagram.read(data);
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


}
