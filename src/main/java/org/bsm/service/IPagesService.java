package org.bsm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.bsm.entity.Pages;
import org.bsm.pagemodel.PagePages;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
public interface IPagesService extends IService<Pages> {

    /**
     * 新增页面
     *
     * @param pagePages 前台传参
     * @return 成功状态
     */
    boolean addPages(PagePages pagePages);
}
