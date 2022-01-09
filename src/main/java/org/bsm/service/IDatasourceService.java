package org.bsm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.bsm.entity.Datasource;
import org.bsm.pagemodel.PageDataSource;
import org.bsm.pagemodel.PageUpload;

import java.io.IOException;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-01-06
 */
public interface IDatasourceService extends IService<Datasource> {
    /**
     * 上传驱动
     */
    String uploadDrive(PageUpload pageUpload) throws IOException;

    /**
     * 测试驱动
     */
    boolean testDrive(PageDataSource pageDataSource);
}
