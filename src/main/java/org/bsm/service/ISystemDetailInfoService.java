package org.bsm.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2021-1-14
 */
public interface ISystemDetailInfoService {

    /**
     * 获取系统详细信息
     *
     * @return JSONObject
     */
    public JSONObject getSystemDetailInfo();


}
