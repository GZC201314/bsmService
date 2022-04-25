package org.bsm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.bsm.entity.Config;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2021-11-09
 */
public interface IConfigService extends IService<Config> {
    /**
     * 新增配置信息
     */
    boolean addConfig(Config config);

    /**
     * 修改配置信息,不允许修改配置名
     */
    boolean editConfig(Config config);

    /**
     * 删除配置信息
     */
    boolean delConfig(Config config);

    /**
     * 批量删除配置信息
     */
    boolean delConfigs(String delIds);

}
