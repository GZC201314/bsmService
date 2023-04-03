package org.bsm.service;

import com.alibaba.fastjson.JSONObject;
import org.bsm.pagemodel.PageFlow;
import org.flowable.spring.SpringProcessEngineConfiguration;

import java.util.List;

public interface IFlowableService {
    /**
     * 查询流程列表
     */
    List<JSONObject> getFlowList();

    boolean deployFlow(PageFlow pageFlow);

}
