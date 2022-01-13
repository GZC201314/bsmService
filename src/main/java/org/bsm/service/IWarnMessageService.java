package org.bsm.service;

import com.alibaba.fastjson.JSONObject;
import org.bsm.pagemodel.PageMessage;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
public interface IWarnMessageService {

    /**
     * 获取告警邮件列表分页信息
     *
     * @param pageMessage 前台传参
     * @return JSONObject
     */
    public JSONObject getWarnMessageList(PageMessage pageMessage);


}
