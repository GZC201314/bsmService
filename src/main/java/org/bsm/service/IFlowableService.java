package org.bsm.service;

import com.alibaba.fastjson.JSONObject;
import org.bsm.pagemodel.PageFlow;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IFlowableService {
    /**
     * 查询流程列表
     */
    JSONObject getFlowList(PageFlow pageFlow);

    boolean deployFlow(PageFlow pageFlow);

    boolean deleteFlows(PageFlow pageFlow);

    void getFlowImg(String id, HttpServletResponse response) throws IOException;

}
