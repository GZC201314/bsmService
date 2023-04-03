package org.bsm.service;

import com.alibaba.fastjson.JSONObject;
import org.bsm.pagemodel.PageFlow;

public interface IFlowableService {
    /**
     * 查询流程列表
     */
    JSONObject getFlowList(PageFlow pageFlow);

    boolean deployFlow(PageFlow pageFlow);

}
