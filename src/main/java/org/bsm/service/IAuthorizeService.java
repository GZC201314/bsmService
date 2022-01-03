package org.bsm.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.bsm.entity.Authorize;
import org.bsm.pagemodel.PagePages;
import org.bsm.pagemodel.PageRole;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
public interface IAuthorizeService extends IService<Authorize> {

    /**
     * 根据rolename来获取当前角色所有的授权页面
     *
     * @param pageRole 前台传参
     * @return JSONObject
     */
    public JSONObject getAllAuthorizePagesByRoleName(PageRole pageRole);

    /**
     * 根据rolename来修改角色的授权页面
     *
     * @param pagePages 前台传参
     * @return boolean
     */
    public boolean updateAuthorizePagesByRoleName(PagePages pagePages);

}
