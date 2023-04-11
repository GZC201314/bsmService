package org.bsm.service;

import com.alibaba.fastjson.JSONObject;
import org.bsm.entity.CurUser;
import org.bsm.pagemodel.PageFlow;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author GZC
 */
public interface IFlowableService {
    /**
     * 查询流程列表
     */
    JSONObject getFlowList(PageFlow pageFlow);

    JSONObject getFlowFormByFlowId(PageFlow pageFlow);

    JSONObject getMyApplicationList(PageFlow pageFlow, CurUser curUser);

    List<JSONObject> getAllFlow(PageFlow pageFlow);


    boolean deployFlow(PageFlow pageFlow);

    JSONObject processInstance(PageFlow pageFlow);

    JSONObject taskDetail(PageFlow pageFlow);

    boolean deleteFlows(PageFlow pageFlow);

    void getFlowImg(String id, HttpServletResponse response) throws IOException;

    void getTaskFlowDiagram(String id, HttpServletResponse response) throws IOException;

}
