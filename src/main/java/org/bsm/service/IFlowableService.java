package org.bsm.service;

import org.flowable.engine.repository.ProcessDefinition;

public interface IFlowableService {

    /**
     * 获取定时任务的详细信息
     */
    ProcessDefinition getFlowInfo();

    boolean startProcess(String name);


}
